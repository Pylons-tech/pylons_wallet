package com.pylons.wallet.core.engine

import com.jayway.jsonpath.JsonPath
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.types.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

internal class TxPylonsAlphaEngine : Engine() {
    override val prefix : String = "__TXPYLONSALPHA__"
    override val usesCrypto: Boolean = true
    override val isDevEngine: Boolean = true
    override val isOffLineEngine: Boolean = false
    var cryptoHandler = CryptoCosmos()
    private val url = """"http://35.224.155.76:80"""

    class TxModel {
        val msg : Array<Object>? = null
        //val fee =
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

    }

    override fun dumpCredentials(credentials: Profile.Credentials) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewCredentials(): Profile.Credentials {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getForeignBalances(id: String): ForeignProfile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOwnBalances(): Profile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewCryptoHandler(): CryptoHandler {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInitialDataSets(): MutableMap<String, MutableMap<String, String>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}