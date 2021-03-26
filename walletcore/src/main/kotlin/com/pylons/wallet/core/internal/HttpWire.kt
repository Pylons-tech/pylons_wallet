package com.pylons.wallet.core.internal

import com.pylons.lib.PubKeyUtil
import com.pylons.lib.types.PylonsSECP256K1
import com.pylons.lib.logging.LogEvent
import com.pylons.lib.logging.Logger
import com.pylons.lib.logging.LogTag
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.spongycastle.util.encoders.Hex
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import com.pylons.lib.klaxon
import com.pylons.wallet.core.engine.TxPylonsEngine

/**
 * Object handling low-level network operations.
 */
object HttpWire {
    private const val RETRIES = 3
    private const val RETRY_DELAY = 1000.toLong()
    var verbose = false

    fun getAddressFromNode (nodeUrl : String, keyPair: PylonsSECP256K1.KeyPair) : String {
        val json = HttpWire.get("$nodeUrl/pylons/addr_from_pub_key/" +
                Hex.toHexString(PubKeyUtil.getCompressedPubkey(keyPair.publicKey()).toArray()))
        return klaxon.parse<TxPylonsEngine.AddressResponse>(json)!!.Bech32Addr!!
    }

    /**
     * Fires an http GET request at the given URL; returns the received page as a string.
     */
    fun get(url: String): String {
        Logger.implementation.log(LogEvent.HTTP_GET, """{"url":"$url"}""", LogTag.info)
        var retryCount = 0
        while (true) {
            with(URL(url).openConnection() as HttpURLConnection) {
                try {
                    requestMethod = "GET"
                    val response = getResponse(inputStream)
                    Logger().log(LogEvent.HTTP_GET, response, LogTag.info)
                    return response
                } catch (e: FileNotFoundException) {
                    Logger().log(LogEvent.HTTP_GET_EXCEPTION, e.toString(), LogTag.error)
                    if (retryCount > RETRIES) {
                        val response = getResponse(errorStream)
                        Logger().log(LogEvent.HTTP_GET, response, LogTag.info)
                        throw e
                    } else {
                        retryCount++
                        Logger().log(LogEvent.HTTP_GET, "Retrying connection... $retryCount/$RETRIES", LogTag.info)
                    }
                } catch (e: Exception) {
                    Logger().log(LogEvent.HTTP_GET_EXCEPTION, e.toString(), LogTag.error)
                    throw e
                } finally {
                    disconnect()
                }
                runBlocking { delay(RETRY_DELAY) }
            }
        }
    }

    /**
     * POSTs the supplied input to the given URL;  returns the received page as a string.
     */
    fun post(url: String, input: String): String {
        println(input)
        Logger.implementation.log(LogEvent.HTTP_POST, """{"url":"$url", "input":$input}""", LogTag.info)
        with(URL(url).openConnection() as HttpURLConnection) {
            try {
                doOutput = true
                requestMethod = "POST"
                val wr = OutputStreamWriter(outputStream)
                wr.write(input)
                wr.flush()
                val response = getResponse(inputStream)
                Logger().log(LogEvent.HTTP_POST, response, LogTag.info)
                return response
            } catch (e: Exception) {
                Logger().log(LogEvent.HTTP_POST_EXCEPTION, e.toString(), LogTag.error)
                if (errorStream != null) {
                    val response = getResponse(errorStream)
                    Logger().log(LogEvent.HTTP_POST, response, LogTag.info)
                    return response
                } else {
                    throw e
                }
            } finally {
                disconnect()
            }
        }
    }

    private fun getResponse(inputStream: InputStream): String {
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