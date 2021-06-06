package tech.pylons.ipc

import com.beust.klaxon.Json
import kotlin.random.Random
import tech.pylons.lib.klaxon
import java.util.*

abstract class DroidIpcWire {


    protected abstract fun writeString(s: String)

    protected abstract fun readString(): String?

    companion object {
        private const val HANDSHAKE_MAGIC = "__PYLONS_WALLET_SERVER"
        private const val HANDSHAKE_REPLY_MAGIC = "__PYLONS_WALLET_CLIENT"

        var implementation: DroidIpcWire? = null

        //according to the libpylons, these values to be in IPCLayer
        var clientId: Int = Random.nextInt()
        var walletId: Int = 0
        var messageId: Int = 0

        data class HandshakeMsg(
            @property:[Json(name = "MAGIC")]
            var MAGIC: String,
            @property:[Json(name = "pid")]
            var pid: String,
            @property:[Json(name = "walletId")]
            var walletId: String
        )

        data class HandshakeReplyMsg(
            @property:[Json(name = "MAGIC_REPLY")]
            var MAGIC_REPLY: String = "",
            @property:[Json(name = "clientId")]
            var clientId: String = "",
            @property:[Json(name = "appName")]
            var appName: String = "",
            @property:[Json(name = "appPkgName")]
            var appPkgName: String = ""
        )

        /**
         * establishConnection(appName: String, appClassName:String, callback:(Boolean) -> Unit)
         * called after ipc connection first establishes.
         * initiates Handshake with wallet
         *
         * @param   appName: String - caller app's Display Name
         * @param   appPkgName: String - app's Package Name
         * @param   callback: func handler - bring the handshake result to caller
         */
        fun establishConnection(appName: String, appPkgName: String, callback: (Boolean) -> Unit) {
            val ret = doHandshake(appName, appPkgName)
            callback(ret)
        }

        /**
         * DoHandshake
         * handshake with apps and wallet when establishing ipc connection
         * apps send cliendId, appName, appClassName to wallet
         * wallet respond with walletId
         *
         * return true if handshake succeed, or false when fails
         */
        private fun doHandshake(appName: String, appPkgName: String): Boolean {
            try {
                val msg = HandshakeReplyMsg(
                    MAGIC_REPLY = HANDSHAKE_REPLY_MAGIC,
                    clientId = clientId.toString(),
                    appName = appName,
                    appPkgName = appPkgName
                )
                val strMsg = klaxon.toJsonString(msg)
                writeMessage("$HANDSHAKE_REPLY_MAGIC$strMsg")
                println("IPC Reply HandShake Msg sent $strMsg")

                val retMsg = readMessage()
                if (retMsg != null) {
                    println("handshake server msg $retMsg")
                    val serverMsg = klaxon.parse<HandshakeMsg>(retMsg.removePrefix(HANDSHAKE_MAGIC))
                    if (serverMsg?.MAGIC == HANDSHAKE_MAGIC) {
                        walletId = serverMsg.walletId.toInt()
                        return true
                    }
                }
            } catch (e: Error) {
                println("DoHandshake error: $e")
            }

            return false
        }

        fun makeRequestMessage(message: Message): String {
            val jsonString = klaxon.toJsonString(message)
            println("request msg: $jsonString")
            val msgJson =
                Base64.getEncoder().encodeToString(
                    jsonString.toByteArray(Charsets.US_ASCII)
                )
            return """{"type":"${message.javaClass.simpleName}", "msg":"$msgJson", "messageId":${messageId++}, "clientId":$clientId, "walletId": $walletId}"""
        }

        fun writeMessage(s: String) {
            //wallet handshake not initiated
            implementation!!.writeString(s)
        }

        fun readMessage(): String? {
            var ret: String?
            var elapsedMillis = 0L
            while (true) {
                ret = implementation!!.readString()
                //should also check whether ret is ""
                if (ret == null || ret == "") {
                    Thread.sleep(100)
                    elapsedMillis += 100L
                } else {
                    //if handshake msg, then handle handshake, else continue flow
                    return ret
                }

                // pls think on it. readMessage only takes 10 sec?
                // --> If it didn't respond for over 1 min, we could regard it has a problem.
                if (elapsedMillis > 60 * 1000L) { // no response for 60 secs
                    println("Error: Talking to wallet is not available at the moment!")
                    return null
                }
            }
        }
    }
}