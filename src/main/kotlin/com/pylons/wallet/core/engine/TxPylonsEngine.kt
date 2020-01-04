package com.pylons.wallet.core.engine

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.Logger
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Execution
import com.pylons.wallet.core.types.Transaction
import com.pylons.wallet.core.types.tx.recipe.*
import com.pylons.wallet.core.types.jsonTemplate.*
import com.pylons.wallet.core.types.tx.StdTx
import com.beust.klaxon.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import java.lang.Exception
import java.lang.StringBuilder
import java.security.Security

internal open class TxPylonsEngine : Engine() {
    init {
        Security.removeProvider("BC")
        Security.addProvider(BouncyCastleProvider())

    }
    class AddressResponse {
        val Bech32Addr : String? = null
    }

    override val prefix : String = "__TXPYLONSALPHA__"
    override val backendType: Backend = Backend.LIVE
    override val usesCrypto: Boolean = true
    override val usesMnemonic: Boolean = true
    override val isDevEngine: Boolean = false
    override val isOffLineEngine: Boolean = false
    var cryptoHandler = CryptoCosmos()
    //internal val GIRISH_TEST_NODE_IP = """http://35.224.155.76:80"""
    //internal val MICHEAL_TEST_NODE_IP = """http://35.238.123.59:80"""
    private val local = """http://127.0.0.1:1317"""
    internal val nodeUrl = getUrl()

    private fun getUrl () : String  {
        return when ((Core.config?.nodes.isNullOrEmpty())) {
            true -> local
            false -> Core.config?.nodes!!.first()
        }
    }

    companion object {
        fun getAddressString (addr : ByteArray) : String {
            return Bech32Cosmos.convertAndEncode("cosmos1", AminoCompat.accAddress(addr))
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
            val jsonObject = klaxon.parse<JsonObject>(response)!!
            val code = jsonObject.long("code")
            if (code != null)
                throw Exception("Node returned error code $code for message - " +
                        "${jsonObject.obj("raw_log")!!.string("message")}")
            it.id = jsonObject.string("txhash")
        })
    }

    private fun postTxJson (json : String) : String {
        Logger().log(json, "request_json")
        Logger().log(nodeUrl, "request_url")
        val response = HttpWire.post("""$nodeUrl/txs""", json)
        Logger().log(response, "request_response")
        return response
    }

    // Engine methods

    override fun applyRecipe(id: String, itemIds : Array<String>): Transaction =
            basicTxHandlerFlow { executeRecipe(id, itemIds, Core.userProfile!!.credentials.address,
                    cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence) }

    override fun commitTx(tx: Transaction): Transaction {
        //val response = HttpWire.post("$url/txs", getJsonForTx(tx))
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dumpCredentials(credentials: Profile.Credentials) {
        val c = credentials as Credentials
        UserData.dataSets[prefix]!!["address"] = c.address
        UserData.dataSets["__CRYPTO_COSMOS__"]!!["key"] = cryptoHandler.keyPair!!.secretKey().bytes().toHexString()
        println("Dumped credentials")
    }

    override fun generateCredentialsFromKeys() : Profile.Credentials {
        val json = HttpWire.get("$nodeUrl/pylons/addr_from_pub_key/${Hex.toHexString(CryptoCosmos.getCompressedPubkey(cryptoHandler.keyPair!!.publicKey()).toArray())}")
        val addrString = klaxon.parse<AddressResponse>(json)!!.Bech32Addr!!
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
        cryptoHandler.generateNewKeys()
        val json = HttpWire.get("$nodeUrl/pylons/addr_from_pub_key/${Hex.toHexString(CryptoCosmos.getCompressedPubkey(cryptoHandler.keyPair!!.publicKey()).toArray())}")
        val addrString = klaxon.parse<AddressResponse>(json)!!.Bech32Addr!!
        return Credentials(addrString)
    }

    override fun getNewCryptoHandler(): CryptoHandler = CryptoCosmos()

    override fun getOwnBalances(): Profile? {
        val json = HttpWire.get("$nodeUrl/auth/accounts/${Core.userProfile!!.credentials.address}")
        val value = (Parser.default().parse(StringBuilder(json)) as JsonObject).obj("value")!!
        val sequence = value.string("sequence")!!.toLong()
        val accountNumber = value.string("account_number")!!.toLong()
        val coins = Coin.listFromJson(value.array("coins"))
        (Core.userProfile?.credentials as Credentials?)?.accountNumber = accountNumber
        (Core.userProfile?.credentials as Credentials?)?.sequence = sequence
        Core.userProfile?.coins = coins
        return Core.userProfile
    }

    override fun getPendingExecutions(): List<Execution> {
        val json = HttpWire.get("$nodeUrl/pylons/list_executions/${Core.userProfile!!.credentials.address}")
        return Execution.getListFromJson(json)
    }

    override fun getPylons(q: Long): Transaction =
            basicTxHandlerFlow { getPylons(q, it.address, cryptoHandler.keyPair!!.publicKey(),
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
        val doc = Parser.default().parse(StringBuilder(response)) as JsonObject
        return Transaction(
                stdTx = StdTx.fromJson((doc.obj("tx")!!).obj("value")!!),
                _id = id
        )
    }

    override fun listRecipes(): Array<Recipe> {
        val json = HttpWire.get("$nodeUrl/pylons/list_recipe/${Core.userProfile!!.credentials.address}")
        return Recipe.getArrayFromJson(json)
    }

    override fun listCookbooks(): Array<Cookbook> {
        val json = HttpWire.get("$nodeUrl/pylons/list_cookbooks/${Core.userProfile!!.credentials.address}")
        return Cookbook.getArrayFromJson(json)
    }

    override fun registerNewProfile(name : String): Transaction {
        cryptoHandler.generateNewKeys()
        Core.userProfile = Profile(credentials = getNewCredentials(), coins = listOf(), items = mutableListOf(), strings = mutableMapOf())
        return getPylons(500)
    }

    override fun sendPylons(q: Long, receiver: String): Transaction =
        basicTxHandlerFlow { sendPylons(q, it.address, receiver, cryptoHandler.keyPair!!.publicKey(),
                it.accountNumber, it.sequence) }

    // Unimplemented engine method stubs

    override fun createRecipe(sender : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : WeightedParamList,
                              rType : Long, toUpgrade : ItemUpgradeParams) : Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun disableRecipe(id: String): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun enableRecipe(id: String): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun createCookbook(name: String, devel: String, desc: String, version: String, supportEmail: String, level: Long, costPerBlock : Long): Transaction {
        throw Exception("Creating cookbooks is not allowed on non-dev tx engine")
    }

    override fun updateCookbook(id: String, devel: String, desc: String, version: String, supportEmail: String): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun updateRecipe(id : String, sender : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : WeightedParamList): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")
}