package com.pylons.wallet.core.engine

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.logging.Logger
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Execution
import com.pylons.wallet.core.types.Transaction
import com.pylons.wallet.core.types.tx.recipe.*
import com.pylons.wallet.core.types.jsonTemplate.*
import com.pylons.wallet.core.types.PylonsSECP256K1 as PylonsSECP256K1
import com.beust.klaxon.*
import com.pylons.wallet.core.logging.LogEvent
import com.pylons.wallet.core.logging.LogTag
import com.pylons.wallet.core.types.tx.item.Item
import com.pylons.wallet.core.types.tx.msg.CheckExecution
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import java.lang.Exception
import java.lang.StringBuilder
import java.security.Security

@ExperimentalUnsignedTypes
open class TxPylonsEngine : Engine() {
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
    override var cryptoHandler: CryptoHandler = CryptoCosmos()
    val cryptoCosmos get() = cryptoHandler as CryptoCosmos

    companion object {
        private val local = """http://127.0.0.1:1317"""
        internal val nodeUrl = getUrl()

        private fun getUrl () : String  {
            return when ((Core.config?.nodes.isNullOrEmpty())) {
                true -> local
                false -> Core.config?.nodes!!.first()
            }
        }

        fun getAddressString (addr : ByteArray) : String {
            return Bech32Cosmos.convertAndEncode("cosmos", AminoCompat.accAddress(addr))
        }

        fun getAddressFromNode (key : PylonsSECP256K1.PublicKey) : String {
            val json = HttpWire.get("$nodeUrl/pylons/addr_from_pub_key/" +
                    Hex.toHexString(CryptoCosmos.getCompressedPubkey(key).toArray()))
            return klaxon.parse<AddressResponse>(json)!!.Bech32Addr!!
        }
    }

    // Credentials stuff

    class Credentials (address : String) : Profile.Credentials (address) {
        var sequence : Long = 0
        var accountNumber : Long = 0

        override fun dumpToMessageData(msg: MessageData) {
            msg.strings[Keys.ADDRESS] = address
        }
    }

    // Wiring

    protected fun basicTxHandlerFlow (func : (Credentials) -> String) : Transaction {
        return Transaction(resolver =  {
            val response = postTxJson(func(Core.userProfile!!.credentials as Credentials))
            val jsonObject = Parser.default().parse(StringBuilder(response)) as JsonObject
            val code = jsonObject.long("code")
            if (code != null)
                throw Exception("Node returned error code $code for message - " +
                        "${jsonObject.obj("raw_log")!!.string("message")}")
            // TODO: we should be doing smth else w/ this jsonobject?
            it.id = jsonObject.string("txhash")
        })
    }

    private fun postTxJson (json : String) : String {
        Logger().log(LogEvent.TX_POST, """{"url":"$nodeUrl","tx":$json}""", LogTag.info)
        val response = HttpWire.post("""$nodeUrl/txs""", json)
        Logger().log(LogEvent.TX_RESPONSE, response, LogTag.info)
        return response
    }

    // Engine methods

    override fun applyRecipe(id: String, itemIds : Array<String>): Transaction =
            basicTxHandlerFlow { executeRecipe(id, itemIds, Core.userProfile!!.credentials.address,
                    cryptoCosmos.keyPair!!.publicKey(), it.accountNumber, it.sequence) }

    override fun checkExecution(id: String, payForCompletion : Boolean): Transaction =
            basicTxHandlerFlow { CheckExecution(id, Core.userProfile!!.credentials.address, payForCompletion).toSignedTx() }

    override fun dumpCredentials(credentials: Profile.Credentials) {
        val c = credentials as Credentials
        UserData.dataSets[prefix]!!["address"] = c.address
        UserData.dataSets["__CRYPTO_COSMOS__"]!!["key"] = cryptoCosmos.keyPair!!.secretKey().bytes()!!.toHexString()
        println("Dumped credentials")
    }

    override fun generateCredentialsFromKeys() : Profile.Credentials {
        val addrString = AccAddress.getAddressFromNode(nodeUrl, cryptoCosmos.keyPair!!)
        return Credentials(addrString)
    }

    override fun generateCredentialsFromMnemonic(mnemonic: String, passphrase: String): Profile.Credentials {
        //val bip39 = Bip39(EnglishDictionary.instance())
        //val seed = bip39.createSeed(mnemonic, passphrase)
        //SECP256K1.SecretKey.

        //cryptoHandler.importKeysFromUserData()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getForeignBalances(id: String): ForeignProfile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInitialDataSets(): MutableMap<String, MutableMap<String, String>> {
        val cryptoTable = mutableMapOf<String, String>()
        val engineTable = mutableMapOf<String, String>()
        return mutableMapOf("__CRYPTO_COSMOS__" to cryptoTable, "__TXPYLONSALPHA__" to engineTable)
    }

    override fun getNewCredentials(): Profile.Credentials {
        //val addrString = getAddressFromNode(cryptoCosmos.keyPair!!.publicKey())
        val addrString = getAddressString(CryptoCosmos.getAddressFromKeyPair(cryptoCosmos.keyPair!!).toArray())
        return Credentials(addrString)
    }

    override fun getNewCryptoHandler(): CryptoHandler = CryptoCosmos()

    override fun getOwnBalances(): Profile? {
        val prfJson = HttpWire.get("$nodeUrl/auth/accounts/${Core.userProfile!!.credentials.address}")
        val itemsJson = HttpWire.get("$nodeUrl/pylons/items_by_sender/${Core.userProfile!!.credentials.address}")
        val value = (Parser.default().parse(StringBuilder(prfJson)) as JsonObject).obj("value")!!
        return when (value.string("address")) {
            "" -> {
                null
            }
            else -> {
                val sequence = value.string("sequence")!!.toLong()
                val accountNumber = value.string("account_number")!!.toLong()
                val coins = Coin.listFromJson(value.array("coins"))
                val valueItems = (Parser.default().parse(StringBuilder(itemsJson)) as JsonObject)
                val items = Item.listFromJson(valueItems.array("Items"))
                val credentials = (Core.userProfile!!.credentials as Credentials)
                credentials.accountNumber = accountNumber
                credentials.sequence = sequence
                Core.userProfile?.coins = coins
                Core.userProfile?.items = items
                return Core.userProfile
            }
        }

    }

    override fun getPendingExecutions(): List<Execution> {
        val json = HttpWire.get("$nodeUrl/pylons/list_executions/${Core.userProfile!!.credentials.address}")
        return Execution.getListFromJson(json)
    }

    override fun getPylons(q: Long): Transaction =
            basicTxHandlerFlow { getPylons(q, it.address, cryptoCosmos.keyPair!!.publicKey(),
                    it.accountNumber, it.sequence) }

    override fun getStatusBlock(): StatusBlock {
        val response = HttpWire.get("$nodeUrl/blocks/latest")
        val jsonObject = (Parser.default().parse(StringBuilder(response)) as JsonObject)
        val height = jsonObject.obj("block_meta")!!.obj("header")!!.long("height")!!
        // TODO: calculate block time (this will be Gross)
        return StatusBlock(height = height, blockTime = 0.0, walletCoreVersion = Core.VERSION_STRING)
    }

    override fun getTransaction(id: String): Transaction {
        val response = HttpWire.get("$nodeUrl/txs/$id")
        return Transaction.parseTransactionResponse(id, response)
    }

    override fun listRecipes(): List<Recipe> {
        val json = HttpWire.get("$nodeUrl/pylons/list_recipe/${Core.userProfile!!.credentials.address}")
        return Recipe.getListFromJson(json)
    }

    override fun listCookbooks(): List<Cookbook> {
        val json = HttpWire.get("$nodeUrl/pylons/list_cookbooks/${Core.userProfile!!.credentials.address}")
        return Cookbook.getListFromJson(json)
    }

    override fun registerNewProfile(name : String, kp : PylonsSECP256K1.KeyPair?): Transaction {
        if (kp == null) cryptoHandler.generateNewKeys()
        else cryptoCosmos.keyPair = kp
        Core.userProfile = Profile(credentials = getNewCredentials(),
                coins = listOf(), strings = mutableMapOf(), items = listOf())
        return getPylons(500)
    }

    override fun sendPylons(q: Long, receiver: String): Transaction =
        basicTxHandlerFlow { sendPylons(q, it.address, receiver, cryptoCosmos.keyPair!!.publicKey(),
                it.accountNumber, it.sequence) }

    // Unimplemented engine method stubs

    override fun createRecipe(sender : String, name : String, cookbookId : String, description: String, blockInterval : Long,
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

    override fun updateRecipe(id : String, sender : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList, outputs: List<WeightedOutput>): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")
}