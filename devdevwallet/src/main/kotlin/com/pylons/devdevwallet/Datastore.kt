package com.pylons.devdevwallet

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.logging.LogEvent
import org.apache.commons.lang3.SystemUtils
import com.pylons.wallet.core.logging.Logger
import com.pylons.wallet.core.logging.LogTag
import com.pylons.wallet.core.types.Backend
import com.pylons.wallet.core.types.Config
import java.io.*
import java.nio.file.*
import java.nio.file.Path

object Datastore {
    private const val saveFilename = "sav"
    private val persistentDir = getPersistentDirectory()
    private val runtimeDir = getRuntimeDirectory()
    private var config = Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317"))

    private fun getRuntimeDirectory () : String {
        val path = Path.of(SystemUtils.USER_DIR)
        if (!Files.exists(path)) {
            Files.createDirectories(path)
            Logger.implementation.log(LogEvent.MISC, path.toString(), LogTag.info)
        }
        return path.toString()
    }

    fun setIp (ip : String) {
        config = Config(Backend.LIVE_DEV, listOf("http://$ip:1317"))
    }

    fun getPersistentDirectory () : String {
        val path = Path.of(SystemUtils.USER_HOME + "\\pylons")
        if (!Files.exists(path)) {
            Files.createDirectories(path)
            Logger.implementation.log(LogEvent.MISC, path.toString(), LogTag.info)
        }
        return path.toString()
    }

    fun clear () {
        val file = File(persistentDir, saveFilename)
        if (file.exists()) file.delete()
    }

    fun load () {
        val saveFile = File(persistentDir, saveFilename)
        if (saveFile.exists()) {
            println("Found save file")
            Core.start(config, saveFile.readText()) // userText, worldText, txText
        }
        else {
            println("No save file found")
            Core.start(config, "")
        }
    }

    private fun writeFile (dir : String, filename : String, contents : String) {
        val file = File(dir, filename)
        println("${file.path} ${file.exists()}")
        if (!file.exists()) file.createNewFile()
        FileOutputStream(file.path).use {
            it.write(contents.toByteArray())
            it.flush()
            it.close()
        }
    }

    fun save () {
        val json = Core.backupUserData()
        println("Saving user data...")
        if (json != null) writeFile(persistentDir, saveFilename, json)
        println("OK")
    }
}