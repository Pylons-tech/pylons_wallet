package com.pylons.wallet.core.types

import com.pylons.wallet.core.logging.LogEvent
import com.pylons.wallet.core.logging.Logger
import com.pylons.wallet.core.logging.LogTag
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

/**
 * Object handling low-level network operations.
 */
object HttpWire {
    private const val RETRIES = 3
    private const val RETRY_DELAY = 1000.toLong()
    var verbose = false

    /**
     * Fires an http GET request at the given URL; returns the received page as a string.
     */
    fun get (url : String) : String {
        Logger.implementation.log(LogEvent.HTTP_GET,"""{"url":"$url"}""", LogTag.info)
        var retryCount = 0
        while (true) {
            with(URL(url).openConnection() as HttpURLConnection) {
                try {
                    requestMethod = "GET"
                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()
                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                        println(response.toString())
                        return response.toString()

                    }
                } catch (e : FileNotFoundException) {
                    if (retryCount > RETRIES) {
                        this.disconnect()
                        throw e
                    }
                    else {
                        retryCount++
                        println("Retrying connection...")
                    }
                }
                runBlocking { delay(RETRY_DELAY) }
            }
        }
    }

    /**
     * POSTs the supplied input to the given URL;  returns the received page as a string.
     */
    fun post (url : String, input : String) : String {
        println(input)
        Logger.implementation.log(LogEvent.HTTP_POST, """{"url":"$url", "input":$input}""", LogTag.info)
        var retryCount = 0
        while (true) {
            with(URL(url).openConnection() as HttpURLConnection) {
                try {
                    doOutput = true
                    requestMethod = "POST"
                    val wr = OutputStreamWriter(outputStream)
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
                        Logger().log(LogEvent.HTTP_POST, response.toString(), LogTag.info)
                        return response.toString()
                    }
                } catch (e : FileNotFoundException) {
                    if (retryCount > RETRIES) {
                        this.disconnect()
                        throw e
                    }
                    else {
                        retryCount++
                        Logger().log(LogEvent.HTTP_GET, "Retrying connection... $retryCount/$RETRIES", LogTag.info)
                    }
                } catch (e:Exception) {
                    Logger().log(LogEvent.HTTP_POST_EXCEPTION, e.toString(), LogTag.error)
                    this.disconnect()
                    throw e
                }
                runBlocking { delay(RETRY_DELAY) }
            }
        }
    }
}