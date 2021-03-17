package com.pylons.wallet.core.engine

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.logging.Logger
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.beust.klaxon.*
import com.pylons.lib.PubKeyUtil
import com.pylons.lib.core.ICryptoHandler
import com.pylons.lib.core.IEngine
import com.pylons.lib.internal.fuzzyLong
import com.pylons.wallet.core.VERSION_STRING
import com.pylons.wallet.core.internal.HttpWire
import com.pylons.wallet.core.logging.LogEvent
import com.pylons.wallet.core.logging.LogTag
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.spongycastle.util.encoders.Hex
import java.io.FileNotFoundException
import java.lang.Exception
import java.lang.StringBuilder
import java.security.Security
import java.util.*
import com.pylons.lib.klaxon
import com.pylons.lib.types.*
import com.pylons.lib.types.credentials.CosmosCredentials
import com.pylons.lib.types.credentials.ICredentials
import com.pylons.lib.types.tx.Coin
import com.pylons.lib.types.tx.Trade
import com.pylons.lib.types.tx.TxData
import com.pylons.lib.types.tx.item.Item
import com.pylons.lib.types.tx.msg.*
import com.pylons.lib.types.tx.recipe.*
import com.pylons.lib.types.tx.trade.TradeItemInput

@ExperimentalUnsignedTypes
open class TxPylonsEngine(core : Core) : Engine(core), IEngine {
    init {
        Security.removeProvider("BC")
        Security.addProvider(BouncyCastleProvider())

    }
    class AddressResponse {
        val Bech32Addr : String? = null
    }

    override val prefix : String = "__TXPYLONSALPHA__"
    override val backendType: Backend = Backend.LIVE
    override val usesMnemonic: Boolean = true
    override val isDevEngine: Boolean = false
    override var cryptoHandler: ICryptoHandler = CryptoCosmos(core)
    val cryptoCosmos get() = cryptoHandler as CryptoCosmos
    internal val nodeUrl = getUrl()

    companion object {
        private val local = """http://127.0.0.1:1317"""

        fun getAddressString (addr : ByteArray) : String {
            return Bech32Cosmos.convertAndEncode("cosmos", AminoCompat.accAddress(addr))
        }
    }

    fun getAddressFromNode (key : PylonsSECP256K1.PublicKey) : String {
        val json = HttpWire.get("$nodeUrl/pylons/addr_from_pub_key/" +
                Hex.toHexString(PubKeyUtil.getCompressedPubkey(key).toArray()))
        return klaxon.parse<AddressResponse>(json)!!.Bech32Addr!!
    }

    private fun getUrl () : String  {
        return when ((core.config?.nodes.isNullOrEmpty())) {
            true -> local
            false -> core.config?.nodes!!.first()
        }
    }

    // Credentials stuff

    // Wiring

    protected fun handleTx (func : (ICredentials) -> String) : Transaction {
        return Transaction(resolver =  {
            val response = postTxJson(func(core.userProfile!!.credentials))
            val jsonObject = Parser.default().parse(StringBuilder(response)) as JsonObject

            val code = jsonObject.int("code")
            if (code != null) {
                it.code = Transaction.ResponseCode.of(code)
                it.raw_log = jsonObject.string("raw_log") ?: "Unknown Error"
                throw Exception("Node returned error code $code for message - ${jsonObject.string("raw_log")}")
            }

            val error = jsonObject.string("error")
            if (error != null) {
                it.code = Transaction.ResponseCode.UNKNOWN_ERROR
                it.raw_log = error
                throw Exception("Node returned error code $code for message - $error")
            }

            // TODO: we should be doing smth else w/ this jsonobject?
            val txhash = jsonObject.string("txhash")
            if (txhash != null) {
                it.id = txhash
            } else {
                it.code = Transaction.ResponseCode.UNKNOWN_ERROR
                it.raw_log = "No TX Hash"
                throw Exception("No TX Hash")
            }

        })
    }

    private fun postTxJson (json : String) : String {
        Logger().log(LogEvent.TX_POST, """{"url":"$nodeUrl/txs","tx":$json}""", LogTag.info)
        val response = HttpWire.post("""$nodeUrl/txs""", json)
        Logger().log(LogEvent.TX_RESPONSE, response, LogTag.info)
        return response
    }

    // Engine methods

    override fun applyRecipe(id: String, itemIds : List<String>): Transaction =
            handleTx {
                ExecuteRecipe(
                        recipeId = id,
                        itemIds = itemIds,
                        sender = it.address
                ).toSignedTx()
            }

    override fun checkExecution(id: String, payForCompletion : Boolean): Transaction =
            handleTx {
                CheckExecution(
                        execId = id,
                        sender = it.address,
                        payToComplete = payForCompletion
                ).toSignedTx()
            }

    override fun createTrade(coinInputs: List<CoinInput>, itemInputs: List<TradeItemInput>,
                             coinOutputs: List<Coin>, itemOutputs: List<Item>, extraInfo: String) =
            handleTx{
                CreateTrade(
                        coinInputs = coinInputs,
                        coinOutputs = coinOutputs,
                        itemInputs = itemInputs,
                        itemOutputs = itemOutputs,
                        extraInfo = extraInfo,
                        sender = it.address
                ).toSignedTx()
            }

    override fun dumpCredentials(credentials: ICredentials) {
        core.userData.dataSets["__CRYPTO_COSMOS__"]!!["key"] = cryptoCosmos.keyPair!!.secretKey().bytes()!!.toHexString()
        println("Dumped credentials")
    }

    override fun fulfillTrade(tradeId : String, itemIds : List<String>)   =
            handleTx{
                FulfillTrade(
                        tradeId = tradeId,
                        itemIds = itemIds,
                        sender = it.address
                ).toSignedTx()
            }

    override fun cancelTrade(tradeId : String)   =
            handleTx{
                CancelTrade(
                        tradeId = tradeId,
                        sender = it.address
                ).toSignedTx()
            }

    override fun generateCredentialsFromKeys() : ICredentials {
        val addrString = getAddressString(PubKeyUtil.getAddressFromKeyPair(cryptoCosmos.keyPair!!).toArray())
        return CosmosCredentials(addrString)
    }

    override fun generateCredentialsFromMnemonic(mnemonic: String, passphrase: String): ICredentials {
        //val bip39 = Bip39(EnglishDictionary.instance())
        //val seed = bip39.createSeed(mnemonic, passphrase)
        //SECP256K1.SecretKey.

        //cryptoHandler.importKeysFromUserData()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInitialDataSets(): MutableMap<String, MutableMap<String, String>> {
        val cryptoTable = mutableMapOf<String, String>()
        val engineTable = mutableMapOf<String, String>()
        return mutableMapOf("__CRYPTO_COSMOS__" to cryptoTable, "__TXPYLONSALPHA__" to engineTable)
    }

    override fun getNewCredentials(): ICredentials {
        //val addrString = getAddressFromNode(cryptoCosmos.keyPair!!.publicKey())
        val addrString = getAddressString(PubKeyUtil.getAddressFromKeyPair(cryptoCosmos.keyPair!!).toArray())
        return CosmosCredentials(addrString)
    }

    override fun getNewCryptoHandler(): ICryptoHandler = CryptoCosmos(core)

    override fun getMyProfileState(): MyProfile? {
        println("myProfile path")
        println(core.userProfile)
        val prfJson = HttpWire.get("$nodeUrl/auth/accounts/${core.userProfile!!.credentials.address}")
        val itemsJson = HttpWire.get("$nodeUrl/pylons/items_by_sender/${core.userProfile!!.credentials.address}")
        val lockedCoinDetails = getLockedCoinDetails()
        val value = (Parser.default().parse(StringBuilder(prfJson)) as JsonObject).obj("result")?.obj("value")!!
        return when (value.string("address")) {
            "" -> null
            else -> {
                val sequence = value.fuzzyLong("sequence")
                val accountNumber = value.fuzzyLong("account_number")
                val coins = Coin.listFromJson(value.array("coins"))
                val valueItems = (Parser.default().parse(StringBuilder(itemsJson)) as JsonObject).obj("result")!!
                val items = Item.listFromJson(valueItems.array("Items"))
                val credentials = core.userProfile!!.credentials as CosmosCredentials
                credentials.accountNumber = accountNumber
                credentials.sequence = sequence
                core.userProfile?.coins = coins
                core.userProfile?.items = items
                core.userProfile?.lockedCoinDetails = lockedCoinDetails
                return core.userProfile
            }
        }
    }

    override fun getProfileState(addr: String): Profile? {
        val prfJson = HttpWire.get("$nodeUrl/auth/accounts/$addr")
        // this seems really wrong
        val itemsJson = HttpWire.get("$nodeUrl/pylons/items_by_sender/$addr")
        val value = (Parser.default().parse(StringBuilder(prfJson)) as JsonObject).obj("result")?.obj("value")!!
        return when (value.string("address")) {
            "" -> null
            else -> {
                val sequence = value.fuzzyLong("sequence")
                val accountNumber = value.fuzzyLong("account_number")
                val coins = Coin.listFromJson(value.array("coins"))
                val valueItems = (Parser.default().parse(StringBuilder(itemsJson)) as JsonObject).obj("result")!!
                val items = Item.listFromJson(valueItems.array("Items"))
                return Profile(
                        address = addr,
                        strings = mapOf(),
                        coins = coins,
                        items = items
                )
            }
        }
    }

    override fun getPendingExecutions(): List<Execution> {
        val json = HttpWire.get("$nodeUrl/pylons/list_executions/${core.userProfile!!.credentials.address}")
        return Execution.getListFromJson(json)
    }

    override fun getPylons(q: Long): Transaction =
            handleTx {
                GetPylons(
                        amount = listOf(Coin("pylon", q)),
                        sender = it.address
                ).toSignedTx()
            }

    override fun getStatusBlock(): StatusBlock {
        val response = HttpWire.get("$nodeUrl/blocks/latest")
        val jsonObject = (Parser.default().parse(StringBuilder(response)) as JsonObject)
        val height = jsonObject.obj("block")!!.obj("header")!!.fuzzyLong("height")!!
        // TODO: calculate block time (this will be Gross)
        return StatusBlock(height = height, blockTime = 0.0, walletCoreVersion = VERSION_STRING)
    }

    override fun getTransaction(id: String): Transaction {
        return try {
            val response = HttpWire.get("$nodeUrl/txs/$id")
            Transaction.parseTransactionResponse(id, response)
        } catch (e : FileNotFoundException) {
            Transaction(
                TxData("", "", listOf()), null, null, null,
                    Transaction.State.TX_NOT_YET_COMMITTED, Transaction.ResponseCode.UNKNOWN_ERROR)
        }
    }

    override fun listRecipes(): List<Recipe> {
        val json = HttpWire.get("$nodeUrl/pylons/list_recipe/${core.userProfile!!.credentials.address}")
        return Recipe.listFromJson(json)
    }

    override fun listTrades(): List<Trade> {
        val json = HttpWire.get("$nodeUrl/pylons/list_trade")
        return Trade.listFromJson(json)
    }

    override fun listCookbooks(): List<Cookbook> {
        val json = HttpWire.get("$nodeUrl/pylons/list_cookbooks/${core.userProfile!!.credentials.address}")
        return Cookbook.getListFromJson(json)
    }

    override fun registerNewProfile(name : String, kp : PylonsSECP256K1.KeyPair?): Transaction {
        if (kp == null) cryptoHandler.generateNewKeys()
        else cryptoCosmos.keyPair = kp
        core.userProfile = MyProfile(core = core, credentials = getNewCredentials(),
                coins = listOf(), strings = mutableMapOf(), items = listOf())
        return createChainAccount()
    }

    override fun createChainAccount(): Transaction =
            handleTx {
                CreateAccount(
                        sender = it.address
                ).toSignedTx()
            }

    override fun googleIapGetPylons(productId: String, purchaseToken: String, receiptData: String, signature: String): Transaction =
            handleTx {
                GoogleIapGetPylons(
                        productId = productId,
                        purchaseToken = purchaseToken,
                        receiptDataBase64 = Base64.getEncoder().encodeToString(receiptData.toByteArray()),
                        signature = signature,
                        sender = it.address
                ).toSignedTx()
            }

    override fun checkGoogleIapOrder(purchaseToken: String): Boolean {
        val response = HttpWire.get("$nodeUrl/pylons/check_google_iap_order/$purchaseToken")
        return (Parser.default().parse(StringBuilder(response)) as JsonObject).obj("result")!!.boolean("exist")!!
    }

    override fun setItemFieldString(itemId: String, field: String, value: String): Transaction =
            handleTx {
                UpdateItemString(
                        itemId = itemId,
                        field = field,
                        value = value,
                        sender = it.address
                ).toSignedTx()
            }

    override fun sendCoins(coins : List<Coin>, receiver: String): Transaction =
            handleTx {
                SendCoins(
                        amount = coins,
                        receiver = receiver,
                        sender = it.address
                ).toSignedTx()
            }

    override fun sendItems(receiver: String, itemIds: List<String>): Transaction =
            handleTx {
                SendItems(
                        itemIds = itemIds,
                        receiver = receiver,
                        sender = it.address
                ).toSignedTx()
            }

    override fun getLockedCoins(): LockedCoin {
        val response = HttpWire.get("$nodeUrl/pylons/get_locked_coins/${core.userProfile!!.credentials.address}")
        return LockedCoin.fromJson((Parser.default().parse(StringBuilder(response)) as JsonObject).obj("result")!!)
    }

    override fun getLockedCoinDetails(): LockedCoinDetails {
        val response = HttpWire.get("$nodeUrl/pylons/get_locked_coin_details/${core.userProfile!!.credentials.address}")
        return LockedCoinDetails.fromJson((Parser.default().parse(StringBuilder(response)) as JsonObject).obj("result")!!)
    }

    // Unimplemented engine method stubs

    override fun createRecipe(name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList,
                              outputs : List<WeightedOutput>) : Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun disableRecipe(id: String): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun enableRecipe(id: String): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun createCookbook(id : String, name: String, developer: String, description: String, version: String, supportEmail: String, level: Long, costPerBlock : Long): Transaction {
        throw Exception("Creating cookbooks is not allowed on non-dev tx engine")
    }

    override fun updateCookbook(id: String, developer: String, description: String, version: String, supportEmail: String): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun updateRecipe(id : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList, outputs: List<WeightedOutput>): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")
}