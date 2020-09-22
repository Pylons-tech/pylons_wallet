package com.pylons.devdevwallet

import java.net.*
import java.nio.charset.Charset
import com.pylons.wallet.core.logging.*
import com.pylons.wallet.ipc.IPCLayer
import com.pylons.wallet.ipc.UILayer
import java.lang.Exception

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        println("starting devdevwallet")
        try {
            var preferredIP = when (args.size) {
                0 -> "127.0.0.1"
                else -> args[0]
            }
            Datastore.setIp(preferredIP)
            Logger.implementation = JvmLogger()
            Datastore.load()
            if (args.isEmpty()) {
                Logger.implementation.log(
                        LogEvent.MISC,
                        "Don't start the dev wallet directly. Pylons clients will start and stop this process automatically. Press Enter to close the application.",
                        LogTag.info)
                System.`in`.read()
            }
            while (true) {
                IPCLayer.getNextMessage { }
            }
        } catch (e : Exception) {
            Logger.implementation.log(LogEvent.REJECT_MESSAGE, e.stackTrace.contentToString(), LogTag.walletError)
            Logger.implementation.log(LogEvent.REJECT_MESSAGE, e.toString(), LogTag.walletError)
        }
    }
}