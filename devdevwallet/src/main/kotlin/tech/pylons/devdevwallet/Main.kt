package tech.pylons.devdevwallet

import tech.pylons.ipc.HttpIpcLayer
import tech.pylons.wallet.core.Multicore
import tech.pylons.lib.logging.*
import tech.pylons.lib.types.Backend
import tech.pylons.lib.types.Config
import tech.pylons.ipc.IPCLayer
import tech.pylons.ipc.UILayer
import java.lang.Exception
import java.security.Security
import org.spongycastle.jce.provider.BouncyCastleProvider
import java.io.PrintWriter
import java.io.StringWriter

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        println("starting devdevwallet")
        IPCLayer.implementation = HttpIpcLayer()
        UILayer.implementation = DevDevWalletUILayer()
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