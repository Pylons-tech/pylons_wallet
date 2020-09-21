package com.pylons.devdevwallet

import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.types.klaxon
import org.apache.commons.codec.binary.Base64
import java.io.*
import java.time.Instant
import java.util.*

class JvmLogger : com.pylons.wallet.core.logging.Logger() {
    private val logTimeout : Long = 60 // in seconds
    private val filename = "log.txt"
    private val dir = Datastore.getPersistentDirectory()

    override fun log(evt : String, msg: String, tag: String) {
        val startTime = Instant.now()
        println("Logger output at $dir\$filename")
        var logLine = Entry(evt, msg, tag)
        println(logLine.serialize())
        val file = File(dir, filename)
        while (true) {
            try {
                println(file.path + file.exists())
                if (!file.exists()) file.createNewFile()
                file.setWritable(true)
                val nl = System.getProperty("line.separator")
                file.appendText(nl + logLine.serialize() + nl)
                break
            } catch (e : FileNotFoundException) {
                if (Instant.now().minusSeconds(logTimeout).isAfter(startTime)) {
                    println("CRITICAL: Logger was unable to grab log file for $logTimeout seconds." +
                            "This should never happen." +
                            "We're going to crash now.")
                    throw e
                }
            }
        }


    }
}