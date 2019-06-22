package com.pylons.wallet.core.engine

import com.jayway.jsonpath.JsonPath
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.Logger
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Transaction
import com.squareup.moshi.*
import org.apache.commons.codec.binary.Base32
import org.apache.tuweni.crypto.SECP256K1
import org.bouncycastle.util.encoders.Hex
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.io.ByteArrayOutputStream
import java.lang.StringBuilder
import kotlin.experimental.and


internal class TxPylonsAlphaEngine : Engine() {
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
        override fun dumpToMessageData(msg: MessageData) {
            msg.strings["id"] = id
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

    private fun get (url : String) : String {
        with(URL(url).openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()
                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                return response.toString()
            }
        }
    }

    private fun post (url : String, input : String) : String {
        System.out.println(input)
        with(URL(url).openConnection() as HttpURLConnection) {
            doOutput = true
            requestMethod = "POST"
            val wr = OutputStreamWriter(outputStream);
            wr.write(input)
            wr.flush()
            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()
                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                return response.toString()
            }
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
        val response = post("$url/txs", getJsonForTx(tx))
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun dumpCredentials(credentials: Profile.Credentials) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewCredentials(): Profile.Credentials {
        val json = get("$url/pylons/addr_from_pub_key/${Hex.toHexString(CryptoCosmos.getCompressedPubkey(cryptoHandler.keyPair!!.publicKey()).toArray())}")
        val addrString = moshi.adapter<AddressResponse>(AddressResponse::class.java).fromJson(json)!!.Bech32Addr!!
        return Credentials(addrString)
    }

    override fun getForeignBalances(id: String): ForeignProfile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOwnBalances(): Profile? {
        val json = get("$url/")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewCryptoHandler(): CryptoHandler = CryptoCosmos()

    override fun getStatusBlock(): StatusBlock {
        val response = get("$url/blocks/latest")
        val height = JsonPath.read<Long>(response, "$.block_meta.header.height")
        // TODO: calculate block time (this will be Gross)
        return StatusBlock(height = height, blockTime = 0.0, walletCoreVersion = Core.VERSION_STRING)
    }

    override fun getTransaction(id: String): Transaction? {
        val response = get("$url/txs/$id")

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerNewProfile(): Profile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPylons(q: Int): Profile? {
        val json = TxJson.getPylons(q, Core.userProfile!!.credentials.id, cryptoHandler.keyPair!!.publicKey(), 4, 1)
        Logger().log(json, "request_json")
        Logger().log(url, "request_url")
        val response = post("""$url/txs""", json)
        Logger().log(response, "request_response")
        return Core.userProfile
    }

    override fun getInitialDataSets(): MutableMap<String, MutableMap<String, String>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}