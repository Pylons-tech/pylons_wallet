package com.pylons.devwallet

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.Logger
import com.pylons.wallet.core.constants.LogTag
import com.pylons.wallet.core.types.Backend
import com.pylons.wallet.core.types.Config
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.concurrent.thread

@ExperimentalUnsignedTypes
object CoreThread {
    val thread : Thread = bootstrapCore()

    private const val saveFilename = "sav"
    private val coreCfg = Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317"))
    private val persistentDir = getPersistentDirectory()

    fun kill () {
        thread.run {
            Logger.implementation.log("killing core thread", LogTag.info)
            stop()
        }
    }

    private fun bootstrapCore () : Thread {
        return thread {
            val saveFile = getSaveFile()
            Core.start(coreCfg, saveFile)
        }
    }

    private fun getPersistentDirectory () : String {
        val path = Path.of(SystemUtils.USER_HOME + "\\pylons")
        if (!Files.exists(path)) {
            Files.createDirectories(path)
            Logger.implementation.log(path.toString(), LogTag.info)
        }
        return path.toString()
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

    private fun getSaveFile () : String {
        val saveFile = File(persistentDir, saveFilename)
        return when (saveFile.exists()) {
            true -> saveFile.readText()
            false -> ""
        }
    }

    private fun save () {
        val json = Core.backupUserData()
        println("Saving user data...")
        if (json != null) writeFile(persistentDir, saveFilename, json)
        println("OK")
    }
}