package com.pylons.ipc

import com.beust.klaxon.Json
import kotlin.random.Random
import com.pylons.lib.klaxon
import java.util.*

abstract class DroidIpcWire {


    protected abstract fun writeString(s: String)

    protected abstract fun readString(): String?

    protected abstract fun doHandshake(s: String): String?

    companion object {
        val HANDSHAKE_MAGIC = "__PYLONS_WALLET_SERVER"
        val HANDSHAKE_REPLY_MAGIC = "__PYLONS_WALLET_CLIENT"

        var implementation: DroidIpcWire? = null

        //according to the libkylon, these values to be in IPCLayer
        var clientId : Int = Random.nextInt()
        var walletId : Int = 0
        var messageId : Int = 0
        var appName = ""

        data class HandshakeMsg(
            @property:[Json(name = "MAGIC")]
            var MAGIC:String,
            @property:[Json(name = "pid")]
            var pid:String,
            @property:[Json(name = "walletId")]
            var walletId:String
        )

        data class HandshakeReplyMsg(
            @property:[Json(name = "MAGIC_REPLY")]
            var MAGIC_REPLY:String="",
            @property:[Json(name = "clientId")]
            var clientId:String="",
            @property:[Json(name = "appName")]
            var appName: String = ""
        )


        fun DoHandshake(){
            val msg = HandshakeReplyMsg(
                MAGIC_REPLY = HANDSHAKE_REPLY_MAGIC,
                clientId = clientId.toString(),
                appName = appName
            )
            val str_msg = klaxon.toJsonString(msg)
            println("IPC HandShake Msg sent ${str_msg}")
            Thread.sleep(1000)
            val ret_msg = implementation!!.doHandshake(str_msg)

            if (ret_msg != null) {
                val server_msg = klaxon.parse<HandshakeMsg>(ret_msg!!)
                if (server_msg?.MAGIC == HANDSHAKE_MAGIC) {
                    walletId = server_msg.walletId.toInt()
                }
            }
            println("IPC HandShake Msg received ${ret_msg}")
        }

        fun makeRequestMessage(message: Message) : String {
            val jsonString = klaxon.toJsonString(message)
            println("request msg: ${jsonString}")
            var msgJson =
                Base64.getEncoder().encodeToString(
                    jsonString.toByteArray(Charsets.US_ASCII))
            return """{"type":"${message.javaClass.simpleName}", "msg":"$msgJson", "messageId":${messageId++}, "clientId":${clientId}, "walletId": ${walletId}}"""
        }

        fun writeMessage(s: String) {
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