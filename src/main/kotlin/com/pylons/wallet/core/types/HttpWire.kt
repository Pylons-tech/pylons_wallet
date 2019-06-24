package com.pylons.wallet.core.types

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object HttpWire {
    fun get (url : String) : String {
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
                System.out.println(response.toString())
                return response.toString()
            }
        }
    }

    fun post (url : String, input : String) : String {
        println(input)
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
}