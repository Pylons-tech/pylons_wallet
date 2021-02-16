package com.pylons.devdevwallet

import com.pylons.wallet.core.Multicore
import com.pylons.wallet.core.logging.*
import com.pylons.wallet.core.types.Backend
import com.pylons.wallet.core.types.Config
import com.pylons.wallet.ipc.IPCLayer
import java.lang.Exception
import java.security.Security
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.OutputStream
import java.io.PrintStream
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.charset.Charset

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        println("starting devdevwallet")
        Security.addProvider(BouncyCastleProvider())
        try {
            var preferredIP = when (args.size) {
                0 -> "127.0.0.1"
                else -> args[0]
            }
            Multicore.enable(Config(Backend.LIVE_DEV, listOf("http://$preferredIP:1317")))
            Logger.implementation = JvmLogger()
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
            var ps = StringWriter()
            var pw = PrintWriter(ps)
            e.printStackTrace(pw)
            Logger.implementation.log(LogEvent.REJECT_MESSAGE, ps.toString(), LogTag.walletError)
            Logger.implementation.log(LogEvent.REJECT_MESSAGE, e.stackTrace.contentToString(), LogTag.walletError)
            Logger.implementation.log(LogEvent.REJECT_MESSAGE, e.toString(), LogTag.walletError)
            Logger.implementation.log(LogEvent.REJECT_MESSAGE, e.cause.toString(), LogTag.walletError)
            Logger.implementation.log(LogEvent.REJECT_MESSAGE, e.cause?.stackTrace?.contentToString() ?: "", LogTag.walletError)
        }
    }
}