package tech.pylons.wallet.core

import Pylonstech.pylons.pylons.TradeOuterClass
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import tech.pylons.ipc.Message
import tech.pylons.ipc.Response
import tech.pylons.lib.*
import tech.pylons.lib.core.ICore
import tech.pylons.lib.core.IEngine
import tech.pylons.lib.core.ILowLevel
import tech.pylons.lib.types.*
import tech.pylons.lib.types.Transaction.Companion.submitAll
import tech.pylons.lib.types.credentials.CosmosCredentials
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.Trade
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.msg.Msg
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.lib.types.tx.trade.TradeItemInput
import kotlinx.coroutines.*

import tech.pylons.wallet.core.engine.*
import tech.pylons.wallet.core.engine.crypto.CryptoCosmos
import tech.pylons.lib.logging.LogEvent
import tech.pylons.lib.logging.LogTag
import tech.pylons.lib.logging.Logger
import tech.pylons.wallet.core.internal.ProtoJsonUtil
import org.apache.tuweni.bytes.Bytes32
import org.spongycastle.util.encoders.Base64
import org.spongycastle.util.encoders.Hex
import tech.pylons.lib.types.tx.msg.CreateRecipe
import tech.pylons.lib.types.tx.trade.ItemRef
import java.io.StringReader

/**
 * The number of times the org.bitcoinj.core.core will retry valid-but-rejected transactions.
 * (For instance: if the remote profile doesn't have the resources to apply a recipe.)
 */
internal const val rejectedTxRetryTimes = 3
/**
 * The amount of time (in milliseconds) to wait before retrying such operations.
 */
internal const val retryDelay : Long = 500 // milliseconds

const val VERSION_STRING = "0.0.1a"

@ExperimentalUnsignedTypes
class Core(val config : Config) : ICore {
    companion object {
        var current : Core? = null
            private set
    }

    override val userData = UserData(this)
    override val lowLevel : ILowLevel = LowLevel(this)
    override var engine: IEngine = NoEngine(this)
    override var userProfile: MyProfile? = null
    internal var profilesBuffer : Set<Profile> = setOf()
    override var sane : Boolean = false
    override var started : Boolean = false
    override var suspendedAction : String? = null
    internal val klaxon = Klaxon()
    override var statusBlock : StatusBlock = StatusBlock(-1, 0.0, VERSION_STRING)

    override var onWipeUserData : (() -> Unit)? = null

    internal fun tearDown () {
        engine = NoEngine(this)
        userProfile = null
        sane = false
        started = false
        onCompletedOperation = null
    }

    /**
     * Serializes persistent user data as a JSON string. All wallet apps will need to take care of calling
     * backupUserData() and storing the results in local storage on their own.
     */
    override fun backupUserData () : String? {
        return when (userProfile) {
            null -> null
            else -> {
                engine.dumpCredentials(userProfile!!.credentials)
                println(userData.dataSets["__CRYPTO_COSMOS__"]!!["key"])
                println(userData.exportAsJson())
                userData.exportAsJson()
            }
        }
    }

    override fun setProfile (myProfile: MyProfile) {
        userProfile = myProfile
    }

    override fun forceKeys (keyString : String, address : String) {
        val engine = engine as TxPylonsEngine
        engine.cryptoCosmos.keyPair =
                PylonsSECP256K1.KeyPair.fromSecretKey(
                        PylonsSECP256K1.SecretKey.fromBytes(Bytes32.wrap(
                                Hex.decode(keyString))))
        userProfile = MyProfile(
                this,
                credentials = CosmosCredentials(address),
                strings = mapOf("name" to "Jack"),
                items = listOf(),
                coins = listOf())
    }

    override fun dumpKeys () : List<String> {
        val cc = engine.cryptoHandler as CryptoCosmos
        return listOf(Hex.toHexString(cc.keyPair!!.secretKey().bytesArray()),
                Hex.toHexString(cc.keyPair!!.publicKey().bytesArray()))
    }

    override fun updateStatusBlock () {
        statusBlock = engine.getStatusBlock()
    }

    override fun use() : Core {
        println("changing org.bitcoinj.core.core")
        current = this
        Msg.useCore(this)
        Message.useCore(this)
        Response.useCore(this)
        return this
    }

    override fun start (userJson : String) {
        engine = when (config.backend) {
            Backend.NONE -> NoEngine(this)
            else -> {
                when (config.devMode) {
                    false -> TxPylonsEngine(this)
                    true -> TxPylonsDevEngine(this)
                }
            }
        }
        runBlocking {
            try {
                userData.parseFromJson(userJson)
                if (userProfile == null) userProfile = when (userJson) {
                    "" -> null
                    else -> MyProfile.fromUserData(this@Core)
                }
            } catch (e : Exception) { // Eventually: we should recover properly from bad data
                Logger.implementation.log(LogEvent.USER_DATA_PARSE_FAIL,
                        """{"message":"${e.message.orEmpty()}","stackTrace":"${e.stackTrace!!.contentDeepToString()}","badUserData":$userJson}""",
                        LogTag.malformedData)
                userData.dataSets = engine.getInitialDataSets()
            }
            sane = true
            started = true
        }
    }

    override var onCompletedOperation : (() -> Unit)? = null

    override fun isReady () : Boolean {
        return sane && started
    }


    //to-do: Tierre cosmos v1beta1 Tx build Json from Tx proto
    /**
     * ref: C:\Users\jin\Downloads\cosmos-client-ts-main\cosmos-client-ts-main\src\rest\cosmos\bank\bank.spec.ts
     * pls refer this tx composition/signing/broadcasting logic
     *
    const msgSend = new cosmos.bank.v1beta1.MsgSend({
    from_address: fromAddress.toString(),
    to_address: toAddress.toString(),
    amount: [{ denom: 'token', amount: '10' }],
    });

    const txBody = new cosmos.tx.v1beta1.TxBody({
    messages: [cosmosclient.codec.packAny(msgSend)],
    });
    const authInfo = new cosmos.tx.v1beta1.AuthInfo({
    signer_infos: [
    {
    public_key: cosmosclient.codec.packAny(pubKey),
    mode_info: {
    single: {
    mode: cosmos.tx.signing.v1beta1.SignMode.SIGN_MODE_DIRECT,
    },
    },
    sequence: account.sequence,
    },
    ],
    fee: {
    gas_limit: cosmosclient.Long.fromString('200000'),
    },
    });

    // sign
    const txBuilder = new cosmosclient.TxBuilder(sdk, txBody, authInfo);
    const signDoc = txBuilder.signDoc(account.account_number);
    txBuilder.addSignature(privKey, signDoc);

    // broadcast
    try {
    const res = await rest.cosmos.tx.broadcastTx(sdk, {
    tx_bytes: txBuilder.txBytes(),
    mode: rest.cosmos.tx.BroadcastTxMode.Block,
    });
    console.log(res);
    } catch (e) {
    console.error(e);
    }
     *
     */
    /**
     * ORG Code:
     *
    val cryptoHandler = (engine as TxPylonsEngine).cryptoHandler
    val signable = baseJsonTemplateForTxSignature(signComponent, sequence, accountNumber, gas)
    Logger().log(LogEvent.SIGNABLE, signable, LogTag.info)
    val signBytes = signable.toByteArray(Charsets.UTF_8)
    val signatureBytes = cryptoHandler.signature(signBytes)
    val signature = Base64.toBase64String(signatureBytes)

    return baseJsonTemplateForTxPost(msg, Base64.toBase64String(PubKeyUtil.getCompressedPubkey(pubkey).toArray()), signature, 400000)
    */

    override fun buildJsonForTxPost(
        msg: String,
        signComponent: String,
        accountNumber: Long,
        sequence: Long,
        pubkey: PylonsSECP256K1.PublicKey,
        gas: Long
    ): String {

        val builder = ProtoJsonUtil.TxProtoBuilder()

        val authInfo = builder.buildAuthInfo(Base64.toBase64String(PubKeyUtil.getCompressedPubkey(pubkey).toArray()),sequence,gas)


        val bodyInfo = builder.buildTxbody(msg)
        builder.buildProtoTxBuilder(bodyInfo, authInfo)
        val signDoc = builder.signDoc(accountNumber, config.chainId)

        val cryptoHandler = (engine as TxPylonsEngine).cryptoHandler
        builder.addSignature(cryptoHandler, signDoc!!)

        println("?")
        println(signDoc)
        println("?")

        return baseTemplateForTxs(builder.txBytes()!!, BroadcastMode.BROADCAST_MODE_BLOCK)
    }

    override fun getRecipe(recipeId: String): Recipe? {
        return engine.getRecipe(recipeId)
    }

    override fun getRecipesByCookbook(cookbookId: String): List<Recipe> {
        return engine.listRecipesByCookbookId(cookbookId)
    }

    fun getProfile() = getProfile(null)

    override fun getProfile (addr : String?) : Profile? {
        return when (addr) {
            // note: both null and "" are valid here. it depends on the serialization behavior
            // on the other side of the ipc link. so we have to check against both.
            null -> engine.getMyProfileState()
            "" -> engine.getMyProfileState()
            else -> engine.getProfileState(addr)
        }
    }

    override fun applyRecipe (creator: String , cookbookID: String, id: String, coinInputsIndex: Long, itemIds : List<String>) : Transaction {
        // HACK: list recipes, then search to find ours
        val arr = engine.listRecipes()
        var r : String? = null
        arr.forEach {
            if (it.cookbookId == cookbookID && it.name == id) {
                r = it.id
            }
        }
        if (r == null) throw java.lang.Exception("Recipe $cookbookID/$id does not exist")
        return engine.applyRecipe(creator!!, cookbookID!!, r!!, coinInputsIndex!!, itemIds!!).submit()
    }

    override fun batchCreateCookbook (creators : List<String>, ids : List<String>, names : List<String>, descriptions : List<String>, developers : List<String>,
                                      versions : List<String>, supportEmails : List<String>, costsPerBlock : List<Coin>, enableds : List<Boolean>) : List<Transaction> {
        val txs = engine.createCookbooks(
            creators = creators,
            ids = ids,
            names = names,
            descriptions = descriptions,
            developers = developers,
            versions = versions,
            supportEmails = supportEmails,
            costsPerBlocks = costsPerBlock,
            enableds = enableds
        ).toMutableList()
        return txs.submitAll()
    }

    override fun batchCreateRecipe (creators: List<String>, cookbooks : List<String>, ids : List<String>, names : List<String>, descriptions : List<String>, versions : List<String>,
                                    coinInputs: List<List<CoinInput>>, itemInputs : List<List<ItemInput>>, outputTables : List<EntriesList>, outputs : List<List<WeightedOutput>>,
                                    blockIntervals : List<Long>, enableds : List<Boolean>, extraInfos: List<String>) : List<Transaction> {
        // klaxon.parse<JsonArray<JsonObject>>


        val txs =  engine.createRecipes(
            creators = creators,
            cookbookIds = cookbooks,
            ids = ids,
            names = names,
            descriptions = descriptions,
            versions = versions,
            coinInputs = coinInputs,
            itemInputs = itemInputs,
            entries = outputTables,
            outputs = outputs,
            blockIntervals = blockIntervals,
            enableds = enableds,
            extraInfos = extraInfos
        ).toMutableList()
        return txs.submitAll()
    }

    override fun batchDisableRecipe (recipes : List<String>) : List<Transaction> {
        val txs = engine.disableRecipes(
            recipes = recipes
        ).toMutableList()
        return txs.submitAll()
    }

    override fun batchEnableRecipe (recipes : List<String>) : List<Transaction> {
        val txs = engine.enableRecipes(
            recipes = recipes
        ).toMutableList()
        return txs.submitAll()
    }

    override fun batchUpdateCookbook (creators : List<String>, ids : List<String>, names : List<String>, descriptions: List<String>, developers: List<String>,
                                      versions : List<String>, supportEmails: List<String>, costPerBlocks: List<Coin>, enableds: List<Boolean>) : List<Transaction> {
        val txs = engine.updateCookbooks(
            creators = creators,
            ids = ids,
            names = names,
            descriptions = descriptions,
            developers = developers,
            versions = versions,
            supportEmails = supportEmails,
            costPerBlocks = costPerBlocks,
            enableds = enableds
        ).toMutableList()
        return txs.submitAll()
    }

    override fun batchUpdateRecipe (creators: List<String>, cookbookIds: List<String>, ids: List<String>, names : List<String>, descriptions: List<String>,
                                    versions: List<String>, coinInputs : List<String>, itemInputs : List<String>,
                                    entries : List<String>, outputs: List<String>, blockIntervals : List<Long>,
                                    enableds: List<Boolean>, extraInfos: List<String>) : List<Transaction> {
        val mItemInputs = mutableListOf<List<ItemInput>>()
        itemInputs.forEach { mItemInputs.add(klaxon.parseArray<ItemInput>(it)?: JsonArray()) }
        val mCoinInputs = mutableListOf<List<CoinInput>>()
        coinInputs.forEach { mCoinInputs.add(CoinInput.listFromJson(klaxon.parse<JsonArray<JsonObject>>(it))) }
        val mOutputTables = mutableListOf<EntriesList>()
        entries.forEach { mOutputTables.add(EntriesList.fromJson(klaxon.parse<JsonObject>(it))!!) }
        val mOutputs = mutableListOf<List<WeightedOutput>>()
        outputs.forEach { mOutputs.add(WeightedOutput.listFromJson(klaxon.parse<JsonArray<JsonObject>>(it))) }
        val txs = engine.updateRecipes(
            creators = creators,
            cookbookIds = cookbookIds,
            ids = ids,
            names = names,
            descriptions = descriptions,
            versions = versions,
            coinInputs = mCoinInputs,
            itemInputs = mItemInputs,
            entries = mOutputTables,
            outputs = mOutputs,
            blockIntervals = blockIntervals,
            enableds = enableds,
            extraInfos = extraInfos
        ).toMutableList()
        return txs.submitAll()
    }

    override fun cancelTrade(tradeId : String) = engine.cancelTrade(tradeId).submit()

    override fun checkExecution(id : String, payForCompletion : Boolean) =
        engine.checkExecution(id, payForCompletion).submit()

    override fun createTrade (creator: String, coinInputs: List<CoinInput>, itemInputs: List<ItemInput>,
                              coinOutputs : List<Coin>, itemOutputs : List<ItemRef>,
                              ExtraInfo : String) : Transaction {
//        val mItemInputs = mutableListOf<ItemInput>()
//        itemInputs.forEach {
//            mItemInputs.add(ItemInput.fromJson(klaxon.parseJsonObject(StringReader(it))))
//        }
//        val mCoinInputs = mutableListOf<CoinInput>()
//        coinInputs.forEach {
//            mCoinInputs.add(CoinInput.fromJson(klaxon.parseJsonObject(StringReader(it))))
//        }
//        val mCoinOutputs = mutableListOf<Coin>()
//        coinOutputs.forEach {
//            mCoinOutputs.add(Coin.fromJson(klaxon.parseJsonObject(StringReader(it))))
//        }
//        val mItemOutputs = mutableListOf<ItemRef>()
//        itemOutputs.forEach {
//            mItemOutputs.add(ItemRef.fromJson(klaxon.parseJsonObject(StringReader(it))))
//        }
        return engine.createTrade(creator, coinInputs, itemInputs, coinOutputs, itemOutputs, ExtraInfo).submit()
    }
    override fun fulfillTrade(creator: String, ID : String, CoinInputsIndex: Long, itemIds : List<ItemRef>) : Transaction =
        engine.fulfillTrade(creator, ID, CoinInputsIndex, itemIds).submit()

    override fun getCookbooks () : List<Cookbook> = engine.listCookbooks()

    override fun getPylons(amount : Long, creator: String)  :  Boolean = engine.getPylons(amount, creator)

    override fun getPendingExecutions () : List<Execution> = engine.getPendingExecutions()

    override fun getRecipes () : List<Recipe> = engine.listRecipes()

    override fun getRecipesBySender() : List<Recipe> = engine.listRecipesBySender()

    override fun getTransaction(txHash : String): Transaction = engine.getTransaction(txHash)

    override fun googleIapGetPylons (productId: String, purchaseToken : String, receiptData : String,
                                 signature : String) : Transaction = engine.googleIapGetPylons(productId,
        purchaseToken, receiptData, signature).submit()

    override fun newProfile (name : String, kp : PylonsSECP256K1.KeyPair?) : Transaction {
        println("kp: $kp")
        return engine.registerNewProfile(name, kp).submit()
    }

    override fun setItemString (itemId : ItemRef, field : String, value : String) =
        engine.setItemFieldString(itemId.itemID, field, value).submit()

    override fun walletServiceTest(string: String): String = "Wallet service test OK input $string"

    override fun walletUiTest() : String = "Wallet UI test OK"

    override fun listCompletedExecutions(): List<Execution> {
        engine.getPendingExecutions()
        TODO("Not yet implemented")
    }

    override fun listTrades(creator: String): List<Trade> {
        return engine.listTrades(creator)
    }

    override fun wipeUserData () {
        tearDown()
        onWipeUserData?.invoke()
    }

    override fun getTrade(tradeId: String): Trade? {
        return engine.getTrade(tradeId)
    }

    override fun getItem(itemId: String): Item? {
        return engine.getItem(itemId)
    }

    override fun listItems(): List<Item> {
        return engine.listItems()
    }

    override fun listItemsBySender(sender: String?): List<Item> {
        return engine.listItemsBySender(sender)
    }

    override fun listItemsByCookbookId(cookbookId: String?): List<Item> {
        return engine.listItemsByCookbookId(cookbookId)
    }

    override fun getCookbook(cookbookId: String): Cookbook? {
        return engine.getCookbook(cookbookId)
    }

    override fun getExecution(executionId: String): Execution? {
        return engine.getExecution(executionId)
    }

    /**
     * Returns the on-chain ID of the recipe with the cookbook and name provided
     */
    override fun getRecipeIdFromCookbookAndName(cookbook: String, name: String): String? {
        getRecipesByCookbook(cookbook).forEach {
            println(it.name)
            if (it.name == name) return it.id
        }
        return null
    }
}