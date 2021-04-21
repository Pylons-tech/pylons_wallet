package com.pylons.wallet.core

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.pylons.ipc.Message
import com.pylons.lib.PubKeyUtil
import com.pylons.lib.baseJsonTemplateForTxPost
import com.pylons.lib.baseJsonTemplateForTxSignature
import com.pylons.lib.core.ICore
import com.pylons.lib.core.IEngine
import com.pylons.lib.core.ILowLevel
import com.pylons.lib.types.*
import com.pylons.lib.types.Transaction.Companion.submitAll
import com.pylons.lib.types.credentials.CosmosCredentials
import com.pylons.lib.types.tx.Coin
import com.pylons.lib.types.tx.Trade
import com.pylons.lib.types.tx.item.Item
import com.pylons.lib.types.tx.msg.Msg
import com.pylons.lib.types.tx.recipe.*
import com.pylons.lib.types.tx.trade.TradeItemInput
import kotlinx.coroutines.*

import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.internal.*
import com.pylons.wallet.core.engine.*
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.lib.logging.LogEvent
import com.pylons.lib.logging.LogTag
import com.pylons.lib.logging.Logger
import org.apache.tuweni.bytes.Bytes32
import org.spongycastle.util.encoders.Base64
import org.spongycastle.util.encoders.Hex
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
        return this
    }

    override fun start (userJson : String) {
        engine = when (config.backend) {
            Backend.LIVE -> TxPylonsEngine(this)
            Backend.LIVE_DEV -> TxPylonsDevEngine(this)
            Backend.NONE -> NoEngine(this)
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

    override fun buildJsonForTxPost(
        msg: String,
        signComponent: String,
        accountNumber: Long,
        sequence: Long,
        pubkey: PylonsSECP256K1.PublicKey,
        gas: Long
    ): String {
        val cryptoHandler = (engine as TxPylonsEngine).cryptoHandler
        val signable = baseJsonTemplateForTxSignature(signComponent, sequence, accountNumber, gas)
        Logger().log(LogEvent.SIGNABLE, signable, LogTag.info)
        val signBytes = signable.toByteArray(Charsets.UTF_8)
        val signatureBytes = cryptoHandler.signature(signBytes)
        val signature = Base64.toBase64String(signatureBytes)
        return baseJsonTemplateForTxPost(msg, Base64.toBase64String(PubKeyUtil.getCompressedPubkey(pubkey).toArray()), signature, 400000)
    }

    fun getProfile() = getProfile(null)

    override fun getProfile (addr : String?) : Profile? {
        return when (addr) {
            // note: both null and "" are valid here. it depends on the serialization behavior
            // on the other side of the ipc link. so we have to check against both.
            null -> engine.getMyProfileState() as Profile?
            "" -> engine.getMyProfileState() as Profile?
            else -> engine.getProfileState(addr)
        }
    }

    override fun applyRecipe (recipe : String, cookbook : String, itemInputs : List<String>) : Transaction {
        // HACK: list recipes, then search to find ours
        val arr = engine.listRecipes()
        var r : String? = null
        arr.forEach {
            if (it.cookbookId == cookbook && it.name == recipe) {
                r = it.id
            }
        }
        if (r == null) throw java.lang.Exception("Recipe $cookbook/$recipe does not exist")
        return engine.applyRecipe(r!!, itemInputs).submit()
    }

    override fun batchCreateCookbook (ids : List<String>, names : List<String>, developers : List<String>, descriptions : List<String>, versions : List<String>,
                                  supportEmails : List<String>, levels : List<Long>, costsPerBlock : List<Long>) : List<Transaction> {
        val txs = engine.createCookbooks(
            ids = ids,
            names = names,
            developers = developers,
            descriptions = descriptions,
            versions = versions,
            supportEmails = supportEmails,
            levels = levels,
            costsPerBlock = costsPerBlock
        ).toMutableList()
        return txs.submitAll()
    }

    override fun batchCreateRecipe (names : List<String>, cookbooks : List<String>, descriptions : List<String>,
                                blockIntervals : List<Long>, coinInputs: List<String>, itemInputs : List<String>,
                                outputTables : List<String>, outputs : List<String>) : List<Transaction> {
        val mItemInputs = mutableListOf<List<ItemInput>>()
        itemInputs.forEach { mItemInputs.add(klaxon.parseArray(it)?: JsonArray()) }
        val mCoinInputs = mutableListOf<List<CoinInput>>()
        coinInputs.forEach { mCoinInputs.add(klaxon.parseArray(it)?: JsonArray()) }
        val mOutputTables = mutableListOf<EntriesList>()
        outputTables.forEach { mOutputTables.add(klaxon.parse(it)?: EntriesList(listOf(), listOf(), listOf())) }
        val mOutputs = mutableListOf<List<WeightedOutput>>()
        outputs.forEach {
            println(it)
            val arr = klaxon.parseArray<WeightedOutput>(it) ?: JsonArray()
            mOutputs.add(arr.toList())
        }
        val txs =  engine.createRecipes(
            names = names,
            cookbookIds = cookbooks,
            descriptions = descriptions,
            blockIntervals = blockIntervals,
            coinInputs = mCoinInputs,
            itemInputs = mItemInputs,
            entries = mOutputTables,
            outputs = mOutputs
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

    override fun batchUpdateCookbook (names : List<String>, developers : List<String>, descriptions : List<String>, versions : List<String>,
                                  supportEmails : List<String>, ids : List<String>) : List<Transaction> {
        val txs = engine.updateCookbooks(
            ids = ids,
            names = names,
            developers = developers,
            descriptions = descriptions,
            versions = versions,
            supportEmails = supportEmails
        ).toMutableList()
        return txs.submitAll()
    }

    override fun batchUpdateRecipe (ids : List<String>, names : List<String>, cookbooks : List<String>, descriptions : List<String>,
                                blockIntervals : List<Long>, coinInputs: List<String>, itemInputs : List<String>,
                                outputTables : List<String>, outputs : List<String>) : List<Transaction> {
        val mItemInputs = mutableListOf<List<ItemInput>>()
        itemInputs.forEach { mItemInputs.add(klaxon.parseArray<ItemInput>(it)?: JsonArray()) }
        val mCoinInputs = mutableListOf<List<CoinInput>>()
        coinInputs.forEach { mCoinInputs.add(CoinInput.listFromJson(klaxon.parse<JsonArray<JsonObject>>(it))) }
        val mOutputTables = mutableListOf<EntriesList>()
        outputTables.forEach { mOutputTables.add(EntriesList.fromJson(klaxon.parse<JsonObject>(it))!!) }
        val mOutputs = mutableListOf<List<WeightedOutput>>()
        outputs.forEach { mOutputs.add(WeightedOutput.listFromJson(klaxon.parse<JsonArray<JsonObject>>(it))) }
        val txs = engine.updateRecipes(
            ids = ids,
            names = names,
            cookbookIds = cookbooks,
            descriptions = descriptions,
            blockIntervals = blockIntervals,
            coinInputs = mCoinInputs,
            itemInputs = mItemInputs,
            entries = mOutputTables,
            outputs = mOutputs
        ).toMutableList()
        return txs.submitAll()
    }

    override fun cancelTrade(tradeId : String) = engine.cancelTrade(tradeId).submit()

    override fun checkExecution(id : String, payForCompletion : Boolean) =
        engine.checkExecution(id, payForCompletion).submit()

    override fun createTrade (coinInputs: List<String>, itemInputs : List<String>,
                          coinOutputs : List<String>, itemOutputs : List<String>,
                          extraInfo : String) : Transaction {
        val mItemInputs = mutableListOf<TradeItemInput>()
        itemInputs.forEach { mItemInputs.add(TradeItemInput.fromJson(klaxon.parse(it)!!)) }
        val mCoinInputs = mutableListOf<CoinInput>()
        coinInputs.forEach { mCoinInputs.add(CoinInput.fromJson(klaxon.parse(it)!!)) }
        val mCoinOutputs = mutableListOf<Coin>()
        coinOutputs.forEach { mCoinOutputs.add(Coin.fromJson(klaxon.parse(it)!!)) }
        val mItemOutputs = mutableListOf<Item>()
        itemOutputs.forEach { mItemOutputs.add(Item.fromJson(klaxon.parse(it)!!)) }
        return engine.createTrade(mCoinInputs, mItemInputs, mCoinOutputs, mItemOutputs, extraInfo).submit()
    }

    override fun fulfillTrade(tradeId : String, itemIds : List<String>) : Transaction =
        engine.fulfillTrade(tradeId, itemIds).submit()

    override fun getCookbooks () : List<Cookbook> = engine.listCookbooks()

    override fun getPendingExecutions () : List<Execution> = engine.getPendingExecutions()

    override fun getPylons (q : Long) : Transaction = engine.getPylons(q).submit()

    override fun getRecipes () : List<Recipe> = engine.listRecipes()

    override fun getTransaction(txHash : String): Transaction = engine.getTransaction(txHash)

    override fun googleIapGetPylons (productId: String, purchaseToken : String, receiptData : String,
                                 signature : String) : Transaction = engine.googleIapGetPylons(productId,
        purchaseToken, receiptData, signature).submit()

    override fun newProfile (name : String, kp : PylonsSECP256K1.KeyPair?) : Transaction {
        println("kp: $kp")
        return engine.registerNewProfile(name, kp).submit()
    }

    override fun sendCoins (coins : String, receiver : String) : Transaction =
        engine.sendCoins(
            Coin.listFromJson(klaxon.parseJsonArray(StringReader(coins)) as JsonArray<JsonObject>),
            receiver).submit()

    override fun setItemString (itemId : String, field : String, value : String) =
        engine.setItemFieldString(itemId, field, value).submit()

    override fun walletServiceTest(string: String): String = "Wallet service test OK input $string"

    override fun walletUiTest() : String = "Wallet UI test OK"

    override fun listCompletedExecutions(): List<Execution> {
        engine.getPendingExecutions()
        TODO("Not yet implemented")
    }

    override fun listTrades(): List<Trade> {
        TODO("Not yet implemented")
    }

    override fun wipeUserData () {
        tearDown()
        onWipeUserData?.invoke()
    }

    override fun getTxHistory(address: String): List<Transaction> {
        return engine.getTxHistory(address)
    }
}