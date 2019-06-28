package com.pylons.wallet.core.engine

import com.jayway.jsonpath.JsonPath
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.Logger
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.internal.retryOnError
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Transaction
import com.squareup.moshi.*
import com.sun.xml.internal.fastinfoset.util.StringArray
import net.minidev.json.JSONArray
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.security.Security


internal class TxPylonsAlphaEngine : Engine() {
    init {
        Security.removeProvider("BC")
        Security.addProvider(BouncyCastleProvider())
    }
    class AddressResponse {
        val Bech32Addr : String? = null
    }

    override val prefix : String = "__TXPYLONSALPHA__"
    override val usesCrypto: Boolean = true
    override val isDevEngine: Boolean = true
    override val isOffLineEngine: Boolean = false
    var cryptoHandler = CryptoCosmos()
    private val url = """http://35.224.155.76:80"""

    companion object {
        val moshi = Moshi.Builder().build()

        fun getAddressString (addr : ByteArray) : String {
            return Bech32Cosmos.convertAndEncode("cosmos", AminoCompat.accAddress(addr))
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

    private fun getJsonForTx (tx : Transaction) : String {
        //val jsonObject =
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }


    override fun applyRecipe(cookbook: String, recipe: String, preferredItemIds: List<String>): Profile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun commitTx(tx: Transaction): Profile? {
        val response = HttpWire.post("$url/txs", getJsonForTx(tx))
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun dumpCredentials(credentials: Profile.Credentials) {
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

    override fun getTransaction(id: String): Transaction? {
        val response = HttpWire.get("$url/txs/$id")

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerNewProfile(): Profile? {
        return getPylons(500) // is this actually gonna work?
    }

    override fun getPylons(q: Int): Profile? {
        val c = Core.userProfile!!.credentials as Credentials
        val json = TxJson.getPylons(q, c.address, cryptoHandler.keyPair!!.publicKey(), c.accountNumber, c.sequence)
        Logger().log(json, "request_json")
        Logger().log(url, "request_url")
        val response = HttpWire.post("""$url/txs""", json)
        Logger().log(response, "request_response")
        return Core.userProfile
    }

    override fun getInitialDataSets(): MutableMap<String, MutableMap<String, String>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}