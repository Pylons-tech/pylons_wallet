package com.pylons.wallet.core.types

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object HttpWire {
    const val RETRIES = 3
    const val RETRY_DELAY = 1000.toLong()

    fun get (url : String) : String {
        var retryCount = 0
        while (true) {
            try {
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
                        println(response.toString())
                        return response.toString()
                    }
                }
            } catch (e : FileNotFoundException) {
                if (retryCount > RETRIES) throw e
                else retryCount++
                runBlocking { delay(RETRY_DELAY) }
            }
        }
    }

    fun post (url : String, input : String) : String {
        println(input)
        var retryCount = 0
        while (true) {
            try {
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
            } catch (e : FileNotFoundException) {
                if (retryCount > RETRIES) throw e
                else retryCount++
                runBlocking { delay(RETRY_DELAY) }
            }
        }
    }
}