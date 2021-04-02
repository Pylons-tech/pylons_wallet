package com.pylons.devdevwallet

import com.pylons.lib.logging.LogEvent
import com.pylons.lib.logging.LogTag
import org.apache.commons.lang3.SystemUtils
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant

class JvmLogger : com.pylons.lib.logging.Logger() {
    private val logTimeout : Long = 60 // in seconds
    private val filename = "log.txt"
    private val dir = getPersistentDirectory()

    private fun getPersistentDirectory () : String {
        val path = Path.of(SystemUtils.USER_HOME + "\\pylons")
        if (!Files.exists(path)) {
            Files.createDirectories(path)
            implementation.log(LogEvent.MISC, path.toString(), LogTag.info)
        }
        return path.toString()
    }

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