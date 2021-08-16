package tech.pylons.wallet.core.engine

import tech.pylons.wallet.core.Core
import tech.pylons.lib.logging.Logger
import tech.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.beust.klaxon.*
import com.google.gson.Gson
import tech.pylons.lib.*
import tech.pylons.lib.constants.QueryConstants
import tech.pylons.lib.core.ICryptoHandler
import tech.pylons.lib.core.IEngine
import tech.pylons.lib.internal.fuzzyLong
import tech.pylons.wallet.core.VERSION_STRING
import tech.pylons.wallet.core.internal.HttpWire
import tech.pylons.lib.logging.LogEvent
import tech.pylons.lib.logging.LogTag
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.spongycastle.util.encoders.Hex
import java.io.FileNotFoundException
import java.lang.Exception
import java.lang.StringBuilder
import java.security.Security
import java.util.*
import tech.pylons.lib.types.*
import tech.pylons.lib.types.credentials.CosmosCredentials
import tech.pylons.lib.types.credentials.ICredentials
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.Trade
import tech.pylons.lib.types.tx.TxData
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.msg.*
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.lib.types.tx.trade.TradeItemInput
import tech.pylons.wallet.core.LowLevel
import tech.pylons.wallet.core.internal.ProtoJsonUtil
import java.io.StringReader

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
    override val usesMnemonic: Boolean = true
    override var cryptoHandler: ICryptoHandler = CryptoCosmos(core)
    val cryptoCosmos get() = cryptoHandler as CryptoCosmos

    companion object {
        private val local = """http://127.0.0.1:1317"""

        fun getAddressString (addr : ByteArray) : String {
            return Bech32Cosmos.convertAndEncode("cosmos", AminoCompat.accAddress(addr))
        }
    }

    fun getAddressFromNode (key : PylonsSECP256K1.PublicKey) : String {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_addr_from_pub_key}" +
                Hex.toHexString(PubKeyUtil.getCompressedPubkey(key).toArray()))
        return klaxon.parse<AddressResponse>(json)!!.Bech32Addr!!
    }

    private fun getUrl () : String  {
        return when ((core.config.nodes.isNullOrEmpty())) {
            true -> local
            false -> core.config.nodes.first()
        }
    }

    // Credentials stuff

    // Wiring

    //modify Transaction Response
    /**
     * {  "tx_response": {    "height": "30707",    "txhash": "C8C06DDB0437666BF6531399F1B875443CB48F4513D5AC8B6398A9A37FC0F3B9",    "codespace": "",    "code": 0,    "data": "0A3D0A0E6372656174655F6163636F756E74122B0A207375636365737366756C6C79206372656174656420746865206163636F756E74120753756363657373",    "raw_log": "[{\"events\":[{\"type\":\"message\",\"attributes\":[{\"key\":\"action\",\"value\":\"create_account\"}]}]}]",    "logs": [      {        "msg_index": 0,        "log": "",        "events": [          {            "type": "message",            "attributes": [              {                "key": "action",                "value": "create_account"              }            ]          }        ]      }    ],    "info": "",    "gas_wanted": "400000",    "gas_used": "33100",    "tx": null,    "timestamp": ""  }}
     */

    protected fun handleTx (func : (ICredentials) -> String) : Transaction {
        return Transaction(resolver =  {
            val response = postTxJson(func(core.userProfile!!.credentials))
            val jsonObject = Parser.default().parse(StringBuilder(response)) as JsonObject
            val txObj = jsonObject.obj("tx_response") ?: throw Exception("Node returned null tx_response")

            val code = txObj.int("code")
            if (code != null && Transaction.ResponseCode.of(code) != Transaction.ResponseCode.OK) {
                it.code = Transaction.ResponseCode.of(code)
                it.raw_log = ErrorMsgUtil.toErrorMsg(txObj.string("raw_log")) ?: "Unknown Error"
                throw Exception("Node returned error code $code for message - ${txObj.string("raw_log")}")
            }

            val error = txObj.string("error")
            if (error != null) {
                it.code = Transaction.ResponseCode.UNKNOWN_ERROR
                it.raw_log = ErrorMsgUtil.toErrorMsg(error)!!
                throw Exception("Node returned error code $code for message - $error")
            }

            // TODO: we should be doing smth else w/ this jsonobject?
            val txhash = txObj.string("txhash")
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
        Logger().log(LogEvent.TX_POST, """{"url":"${LowLevel.getUrlForQueries()}/cosmos/tx/v1beta1/txs","tx":$json}""", LogTag.info)
        val response = HttpWire.post("""${LowLevel.getUrlForQueries()}/cosmos/tx/v1beta1/txs""", json)
        val jo = klaxon.parseJsonObject(StringReader(response))
        if (jo.obj("tx_response")?.int("code") == 4) {
            Logger().log(LogEvent.MISC, """{"tx_rejected":"${jo.obj("tx_response")?.string("raw_log")}"}""", LogTag.error)
        }
        Logger().log(LogEvent.TX_RESPONSE, response, LogTag.info)
        return response
    }

    // Engine methods

    override fun applyRecipe(id: String, itemIds : List<String>, paymentId: String): Transaction =
            handleTx {
                ExecuteRecipe(
                        recipeId = id,
                        itemIds = itemIds,
                        sender = it.address,
                        paymentId = paymentId
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

    override fun fulfillTrade(tradeId : String, itemIds : List<String>, paymentId: String)   =
            handleTx{
                FulfillTrade(
                        tradeId = tradeId,
                        itemIds = itemIds,
                        sender = it.address,
                        paymentId = paymentId
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
        val prfJson = HttpWire.get("${LowLevel.getUrlForQueries()}/auth/accounts/${core.userProfile!!.credentials.address}")
        val itemsJson = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_items_by_sender}${core.userProfile!!.credentials.address}")
        val balanceJson = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_balance}${core.userProfile!!.credentials.address}")


        val lockedCoinDetails = getLockedCoinDetails()
        val value = (Parser.default().parse(StringBuilder(prfJson)) as JsonObject).obj("result")?.obj("value")!!
        return when (value.string("address")) {
            null -> null
            "" -> null
            null -> null //if profile not exists return null
            else -> {
                val sequence = value.fuzzyLong("sequence")
                val accountNumber = value.fuzzyLong("account_number")
                val amount = (Parser.default().parse(StringBuilder(balanceJson)) as JsonObject).string("balance")!!.toLong()
                val coins = listOf(Coin("pylon", amount ))
                val valueItems = (Parser.default().parse(StringBuilder(itemsJson)) as JsonObject)
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
        val prfJson = HttpWire.get("${LowLevel.getUrlForQueries()}/auth/accounts/$addr")
        // this seems really wrong
        val itemsJson = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_items_by_sender}$addr")
        // retrieve balance
        val balanceJson = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_balance}$addr")


        val value = (Parser.default().parse(StringBuilder(prfJson)) as JsonObject).obj("result")?.obj("value")!!
        return when (value.string("address")) {
            "" -> null
            else -> {
                val sequence = value.fuzzyLong("sequence")
                val accountNumber = value.fuzzyLong("account_number")
                val amount = (Parser.default().parse(StringBuilder(balanceJson)) as JsonObject).string("balance")!!.toLong()
                val coins = listOf(Coin("pylon", amount ))
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
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_list_executions}${core.userProfile!!.credentials.address}")
        return Execution.getListFromJson(json)
    }

    override fun getCompletedExecutions(): List<Execution> {
        // one of these should not work
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_list_executions}${core.userProfile!!.credentials.address}")
        return Execution.getListFromJson(json)
    }

    override fun getExecution(executionId: String): Execution? {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_get_execution}${executionId}")
        return Execution.parseFromJson(json)
    }


    override fun getItem(itemId: String): Item? {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_get_item}${itemId}")
        val itemObj = (Parser.default().parse(StringBuilder(json)) as JsonObject)
        val obj = itemObj.obj("item")
        if(obj != null){
            return Item.fromJson(obj)
        }
        return null
    }

    override fun listItems(): List<Item> {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_items_by_sender}")
        val itemObj = (Parser.default().parse(StringBuilder(json)) as JsonObject)
        val items = Item.listFromJson(itemObj.array("Items"))
        return items
    }

    override fun listItemsByCookbookId(cookbookId: String?): List<Item> {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_items_by_cookbook}${cookbookId}")
        val itemObj = (Parser.default().parse(StringBuilder(json)) as JsonObject)
        val items = Item.listFromJson(itemObj.array("Items"))
        return items
    }

    override fun listItemsBySender(sender: String?): List<Item> {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_items_by_sender}${sender}")
        val itemObj = (Parser.default().parse(StringBuilder(json)) as JsonObject)
        val items = Item.listFromJson(itemObj.array("Items"))
        return items
    }

    override fun getPylons(q: Long): Transaction =
            handleTx {
                GetPylons(
                        amount = listOf(Coin("pylon", q)),
                        sender = it.address
                ).toSignedTx()
            }

    override fun getStatusBlock(): StatusBlock {
        val response = HttpWire.get("${LowLevel.getUrlForTxs()}/blocks/latest")
        val jsonObject = (Parser.default().parse(StringBuilder(response)) as JsonObject)
        val height = jsonObject.obj("block")!!.obj("header")!!.fuzzyLong("height")
        // TODO: calculate block time (this will be Gross)
        return StatusBlock(height = height, blockTime = 0.0, walletCoreVersion = VERSION_STRING)
    }

    override fun getTransaction(id: String): Transaction {
        return try {
            val response = HttpWire.get("${LowLevel.getUrlForTxs()}/txs/$id")
            //Transaction.parseTransactionResponse(id, response)
            val dataString = ProtoJsonUtil.TxProtoResponseParser(response)
            Transaction.parseTransactionResponse(id, response, dataString)

        } catch (e : FileNotFoundException) {
            Transaction(
                TxData("", "", listOf()), null, null, null,
                    Transaction.State.TX_NOT_YET_COMMITTED, Transaction.ResponseCode.UNKNOWN_ERROR)
        }
    }

    override fun listRecipes(): List<Recipe> {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_list_recipe}")
        return Recipe.listFromJson(json)
    }

    override fun listRecipesBySender(): List<Recipe> {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_list_recipe}${core.userProfile!!.credentials.address}")
        return Recipe.listFromJson(json)
    }

    override fun listTrades(): List<Trade> {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_list_trade}")
        return Trade.listFromJson(json)
    }

    override fun listCookbooks(): List<Cookbook> {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_list_cookbook}${core.userProfile!!.credentials.address}")
        return Cookbook.getListFromJson(json)
    }

    override fun getCookbook(cookbookId: String): Cookbook? {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_get_cookbook}${cookbookId}")
        return Cookbook.parseFromJson(json)
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
        val response = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_check_google_iap_order}$purchaseToken")
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
        val response = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_get_locked_coins}${core.userProfile!!.credentials.address}")
        return LockedCoin.fromJson((Parser.default().parse(StringBuilder(response)) as JsonObject))
    }

    override fun getLockedCoinDetails(): LockedCoinDetails {
        val response = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_get_locked_coin_details}${core.userProfile!!.credentials.address}")
        return LockedCoinDetails.fromJson((Parser.default().parse(StringBuilder(response)) as JsonObject))
    }

    // Unimplemented engine method stubsf

    override fun createRecipe(name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList,
                              outputs : List<WeightedOutput>, extraInfo: String) : Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun disableRecipe(id: String): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun enableRecipe(id: String): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun createCookbook(id : String, name: String, developer: String, description: String, version: String, supportEmail: String, costPerBlock : Long): Transaction {
        throw Exception("Creating cookbooks is not allowed on non-dev tx engine")
    }

    override fun updateCookbook(id: String, developer: String, description: String, version: String, supportEmail: String): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun updateRecipe(id : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList, outputs: List<WeightedOutput>, extraInfo: String): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun getRecipe(recipeId: String): Recipe? {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_get_recipe}$recipeId")

        val jsonObject = (Parser.default().parse(StringBuilder(json)) as JsonObject)
        return Recipe(
            nodeVersion = jsonObject.string("NodeVersion")!!,
            cookbookId = jsonObject.string("CookbookID")!!,
            name = jsonObject.string("Name")!!,
            id = jsonObject.string("ID")!!,
            description = jsonObject.string("Description")!!,
            sender = jsonObject.string("Sender")!!,
            blockInterval = jsonObject.fuzzyLong("BlockInterval"),
            disabled = jsonObject.boolean("Disabled")!!,
            coinInputs = CoinInput.listFromJson(jsonObject.array("CoinInputs")),
            itemInputs = ItemInput.listFromJson(jsonObject.array("ItemInputs")),
            entries = EntriesList.fromJson(jsonObject.obj("Entries"))!!,
            outputs = WeightedOutput.listFromJson(jsonObject.array("Outputs")),
            extraInfo = jsonObject.string("ExtraInfo")!!
        )
    }

    override fun listRecipesByCookbookId(cookbookId: String): List<Recipe> {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_list_recipe_by_cookbook}$cookbookId")
        return Recipe.listFromJson(json)
    }

    override fun getTrade(tradeId: String): Trade? {
        val json = HttpWire.get("${LowLevel.getUrlForQueries()}${QueryConstants.URL_get_trade}$tradeId")
        return Trade.fromJson(json)
    }


}