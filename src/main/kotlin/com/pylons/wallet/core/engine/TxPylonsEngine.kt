package com.pylons.wallet.core.engine

import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.Logger
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Transaction
import com.squareup.moshi.*
import net.minidev.json.JSONArray
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import java.lang.Exception
import java.security.Security
import org.nightcode.bip39.*
import org.nightcode.bip39.dictionary.*

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
    protected open val url = """http://35.224.155.76:80"""

    companion object {
        val moshi = Moshi.Builder().build()

        fun getAddressString (addr : ByteArray) : String {
            return Bech32Cosmos.convertAndEncode("cosmos1", AminoCompat.accAddress(addr))
        }
    }

    class Credentials (address : String) : Profile.Credentials (address) {
        var sequence : Int = 0
        var accountNumber : Int = 0

        override fun dumpToMessageData(msg: MessageData) {
            msg.strings["address"] = address
        }
    }

    class CredentialsAdapter {
        @FromJson
        fun fromJson (json : String) : Profile.Credentials {
            return moshi.adapter<Credentials>(Credentials::class.java).fromJson(json)!!
        }

        @ToJson
        fun toJson (credentials : Profile.Credentials) : String {
            return moshi.adapter<Credentials>(Credentials::class.java).toJson(credentials as Credentials)!!
        }
    }

    override fun applyRecipe(cookbook: String, recipe: String, preferredItemIds: List<String>): Transaction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createCookbook(name: String, devel: String, desc: String, version: String, supportEmail: String, level: Int): Transaction {
        throw Exception("Creating cookbooks is not allowed on non-dev tx engine")
    }

    override fun commitTx(tx: Transaction): Transaction {
        //val response = HttpWire.post("$url/txs", getJsonForTx(tx))
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun dumpCredentials(credentials: Profile.Credentials) {
        val c = credentials as Credentials
        UserData.dataSets[prefix]!!["address"] = c.address
        UserData.dataSets["__CRYPTO_COSMOS__"]!!["key"] = cryptoHandler.keyPair!!.secretKey().bytes().toHexString()
    }

    override fun generateCredentialsFromMnemonic(mnemonic: String, passphrase: String): Profile.Credentials {
        val bip39 = Bip39(EnglishDictionary.instance())
        val seed = bip39.createSeed(mnemonic, passphrase)
        //SECP256K1.SecretKey.

        cryptoHandler.importKeysFromUserData()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateCredentialsFromKeys() : Profile.Credentials {
        val json = HttpWire.get("$url/pylons/addr_from_pub_key/${Hex.toHexString(CryptoCosmos.getCompressedPubkey(cryptoHandler.keyPair!!.publicKey()).toArray())}")
        val addrString = moshi.adapter<AddressResponse>(AddressResponse::class.java).fromJson(json)!!.Bech32Addr!!
        return Credentials(addrString)
    }

    override fun getNewCredentials(): Profile.Credentials {
        cryptoHandler.generateNewKeys()
        val json = HttpWire.get("$url/pylons/addr_from_pub_key/${Hex.toHexString(CryptoCosmos.getCompressedPubkey(cryptoHandler.keyPair!!.publicKey()).toArray())}")
        val addrString = moshi.adapter<AddressResponse>(AddressResponse::class.java).fromJson(json)!!.Bech32Addr!!
        return Credentials(addrString)
    }

    override fun getForeignBalances(id: String): ForeignProfile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOwnBalances(): Profile? {
        val json = HttpWire.get("$url/auth/accounts/${Core.userProfile!!.credentials.address}")
        val sequence = JsonPath.read<String>(json, "$.value.sequence").toInt()
        val accountNumber = JsonPath.read<String>(json, "$.value.account_number").toInt()
        val denoms = JsonPath.read<JSONArray>(json, "$.value.coins.*.denom")
        val amounts = JsonPath.read<JSONArray>(json, "$.value.coins.*.amount")
        val coins = mutableMapOf<String, Int>()
        for (i in 0 until denoms.size) {
            coins[denoms[i].toString()] = amounts[i].toString().toInt()
        }
        (Core.userProfile?.credentials as Credentials?)?.accountNumber = accountNumber
        (Core.userProfile?.credentials as Credentials?)?.sequence = sequence
        Core.userProfile?.coins = coins
        return Core.userProfile
    }

    override fun getNewCryptoHandler(): CryptoHandler = CryptoCosmos()

    override fun getStatusBlock(): StatusBlock {
        val response = HttpWire.get("$url/blocks/latest")
        val height = JsonPath.read<Long>(response, "$.block_meta.header.height")
        // TODO: calculate block time (this will be Gross)
        return StatusBlock(height = height, blockTime = 0.0, walletCoreVersion = Core.VERSION_STRING)
    }

    override fun getTransaction(id: String): Transaction {
        val response = HttpWire.get("$url/txs/$id")
        return when (val msgType =JsonPath.read<String>(response, "$.tx.value.msg[0].type")) {
            "pylons/GetPylons" -> {
                val requester = JsonPath.read<String>(response, "$.tx.value.msg[0].value.Requester")
                val pylons = JsonPath.read<String>(response, "$.tx.value.msg[0].value.Amount[0].amount")
                Transaction(requester, Transaction.MsgGetPylons(pylons.toLong()), msgType, id)
            }
            "pylons/SendPylons" -> {
                val pylons = JsonPath.read<String>(response, "$.tx.value.msg[0].value.Amount[0].amount")
                val sender = JsonPath.read<String>(response, "$.tx.value.msg[0].value.Sender")
                val receiver = JsonPath.read<String>(response, "$.tx.value.msg[0].value.Receiver")
                Transaction(sender, Transaction.MsgSendPylons(pylons.toLong(), sender, receiver), msgType, id)
            }
            else -> throw Exception("Unrecognized message type")
        }
    }

    override fun registerNewProfile(name : String): Transaction {
        cryptoHandler.generateNewKeys()
        Core.userProfile = Profile(credentials = getNewCredentials(), coins = mutableMapOf(), items = mutableListOf(), strings = mutableMapOf())
        return getPylons(500)
    }

    override fun getPylons(q: Int): Transaction =
            basicTxHandlerFlow { TxJson.getPylons(q, it.address, cryptoHandler.keyPair!!.publicKey(),
                    it.accountNumber, it.sequence) }

    override fun getInitialDataSets(): MutableMap<String, MutableMap<String, String>> {
        val cryptoTable = mutableMapOf<String, String>()
        val engineTable = mutableMapOf<String, String>()
        return mutableMapOf("__CRYPTO_COSMOS__" to cryptoTable, "__TXPYLONSALPHA__" to engineTable)
    }

    override fun sendPylons(q: Int, receiver: String): Transaction =
        basicTxHandlerFlow { TxJson.sendPylons(q, it.address, receiver, cryptoHandler.keyPair!!.publicKey(),
                it.accountNumber, it.sequence) }

    protected fun basicTxHandlerFlow (func : (Credentials) -> String) : Transaction {
        return Transaction(resolver =  {
            val response = postTxJson(func(Core.userProfile!!.credentials as Credentials))
            try {
                val code = JsonPath.read<Int>(response, "$.code")
                if (code != null)
                    throw Exception("Node returned error code $code for message - ${JsonPath.read<String>(response, "$.raw_log.message")}")
            } catch (e : PathNotFoundException) {
                // swallow this - we only find an error code if there is in fact an error
            }
            it.id = JsonPath.read(response, "$.txhash")
        })
    }

    override fun updateCookbook(id: String, devel: String, desc: String, version: String, supportEmail: String): Transaction =
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    override fun createRecipe(name : String, cookbookName: String, desc: String, inputs: Map<String, Int>, outputs: Map<String, Int>, time: Int): Transaction=
            throw Exception("Updating cookbooks is not allowed on non-dev tx engine")

    private fun postTxJson (json : String) : String {
        Logger().log(json, "request_json")
        Logger().log(url, "request_url")
        val response = HttpWire.post("""$url/txs""", json)
        Logger().log(response, "request_response")
        return response
    }
}