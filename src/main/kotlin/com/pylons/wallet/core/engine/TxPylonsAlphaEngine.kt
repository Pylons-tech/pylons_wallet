package com.pylons.wallet.core.engine

import com.jayway.jsonpath.JsonPath
import com.lambdaworks.codec.Base64
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.Logger
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Transaction
import com.squareup.moshi.*
import org.apache.commons.codec.binary.Base32
import org.apache.tuweni.crypto.SECP256K1
import org.bitcoinj.core.*
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.Networks
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
    override val prefix : String = "__TXPYLONSALPHA__"
    override val usesCrypto: Boolean = true
    override val isDevEngine: Boolean = true
    override val isOffLineEngine: Boolean = false
    var cryptoHandler = CryptoCosmos()
    private val url = """http://35.224.155.76:80"""

    class Credentials (address : String) : Profile.Credentials (address) {
        override fun dumpToMessageData(msg: MessageData) {
            msg.strings["id"] = id
        }
    }

    class CredentialsAdapter {
        @FromJson
        fun fromJson (json : String) : Profile.Credentials {
            return Moshi.Builder().build().adapter<Credentials>(Credentials::class.java).fromJson(json)!!
        }

        @ToJson
        fun toJson (credentials : Profile.Credentials) : String {
            return Moshi.Builder().build().adapter<Credentials>(Credentials::class.java).toJson(credentials as Credentials)!!
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
        val address = CryptoCosmos.getAddressFromKeyPair(cryptoHandler.keyPair!!).toArray()
        System.out.println("addr:")
        System.out.println(address.size)
        System.out.println(Base64.encode(address))
        System.out.println(Hex.toHexString(Base32().encode(address)))
        var a = SegwitAddress.fromKey(NetworkParameters.fromID(NetworkParameters.ID_MAINNET), ECKey.fromPrivate(cryptoHandler.keyPair!!.secretKey().bytesArray()))
        //System.out.println(a.toBech32())

        val f = Bech32.encode("cosmos", Bech32.decode(a.toBech32()).data)
        //SegwitAddress.fromString()
        return Credentials(f)
    }

    override fun getForeignBalances(id: String): ForeignProfile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOwnBalances(): Profile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewCryptoHandler(): CryptoHandler = CryptoCosmos()

    override fun getNewTransactionId(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewUserId(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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
        val json = getGetPylonsJson(q.toString(), Core.userProfile!!.credentials.id, cryptoHandler.keyPair!!)
        Logger().log(json, "request_json")
        System.out.println(json)
        Logger().log(url, "request_url")
        post("""$url/txs""", json)
        return Core.userProfile
    }

    override fun getInitialDataSets(): MutableMap<String, MutableMap<String, String>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun strFromBase64 (base64 : CharArray) : String {
        val sb = StringBuilder()
        base64.forEach { sb.append(it) }
        return sb.toString()
    }

    fun getGetPylonsJson (amount : String, address : String, keyPair : SECP256K1.KeyPair) : String{
        val msg = """
            [
            {
                "type": "pylons/GetPylons",
                "value": {
                "Amount": [
                {
                    "denom": "pylon",
                    "amount": "$amount"
                }
                ],
                "Requester": "$address"
            }
            }
            ]
        """.trimIndent()
        val pubkey = strFromBase64(Base64.encode(keyPair.publicKey().bytesArray()))
        val signature = strFromBase64(Base64.encode(cryptoHandler.signature(msg.toByteArray(charset = Charset.defaultCharset()))))
        return """{
        "tx": {
            "msg": $msg,
            "fee": {
            "amount": null,
            "gas": "200000"
        },
            "signatures": [
            {
                "pub_key": {
                "type": "tendermint/PubKeySecp256k1",
                "value": "${pubkey}"
            },
                "signature": "${signature}"
            }
            ],
            "memo": ""
        },
        "mode": "sync"
    }""".trimIndent()
    }

}