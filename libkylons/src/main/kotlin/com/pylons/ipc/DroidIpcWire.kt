package com.pylons.ipc

import com.beust.klaxon.Json
import kotlin.random.Random
import com.pylons.lib.klaxon
import java.util.*

abstract class DroidIpcWire {


    protected abstract fun writeString(s: String)

    protected abstract fun readString(): String?

    companion object {
        val HANDSHAKE_MAGIC = "__PYLONS_WALLET_SERVER"
        val HANDSHAKE_REPLY_MAGIC = "__PYLONS_WALLET_CLIENT"

        var implementation: DroidIpcWire? = null

        //according to the libkylon, these values to be in IPCLayer
        var clientId: Int = Random.nextInt()
        var walletId: Int = 0
        var messageId: Int = 0
        var appName = ""
        var appPkgName = ""

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
        fun doHandshake(appName: String, appPkgName: String): Boolean {
            Companion.appName = appName
            Companion.appPkgName = appPkgName
            try {
                val msg = HandshakeReplyMsg(
                    MAGIC_REPLY = HANDSHAKE_REPLY_MAGIC,
                    clientId = clientId.toString(),
                    appName = appName,
                    appPkgName = appPkgName
                )
                val str_msg = klaxon.toJsonString(msg)
                writeMessage("${HANDSHAKE_REPLY_MAGIC}${str_msg}")
                println("IPC Reply HandShake Msg sent ${str_msg}")

                val ret_msg = readMessage()
                if (ret_msg != null) {
                    val server_msg = klaxon.parse<HandshakeMsg>(ret_msg.removePrefix(HANDSHAKE_MAGIC)!!)
                    if (server_msg?.MAGIC == HANDSHAKE_MAGIC) {
                        walletId = server_msg.walletId.toInt()
                        return true
                    }
                }
            } catch (e: Error) {
                println("DoHandshake error: ${e}")
            }

            return false
        }

        fun makeRequestMessage(message: Message): String {
            val jsonString = klaxon.toJsonString(message)
            println("request msg: ${jsonString}")
            var msgJson =
                Base64.getEncoder().encodeToString(
                    jsonString.toByteArray(Charsets.US_ASCII)
                )
            return """{"type":"${message.javaClass.simpleName}", "msg":"$msgJson", "messageId":${messageId++}, "clientId":${clientId}, "walletId": ${walletId}}"""
        }

        fun writeMessage(s: String) {
            //wallet handshake not intiated
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

                //pls think on it. readMessage only takes 10 sec?
                //if (elapsedMillis > 10 * 1000L) { // no response for 10 secs
                //    println("Error: Wallet is busy or connection to wallet is unavailable. Try again later!")
                //    return null
                //}
            }
        }
    }
}