package com.pylons.ipc

import com.pylons.lib.logging.LogEvent
import com.pylons.lib.logging.LogTag
import com.pylons.lib.logging.Logger
import org.apache.commons.codec.binary.Hex
import java.net.ServerSocket
import java.nio.ByteBuffer
import java.nio.charset.Charset

private const val HANDSHAKE_MAGIC = "__PYLONS_WALLET_SERVER"
private const val HANDSHAKE_REPLY_MAGIC = "__PYLONS_WALLET_CLIENT"

object HttpIpcWire {
    private val ascii = Charset.forName("US-ASCII")

    private fun readNext(): ByteArray? {
        Logger.implementation.log(LogEvent.AWAIT_TCP, "{\"info\":\"reading bytes from client\"}", LogTag.info)
        ServerSocket(50001).use { serverSocket ->
            serverSocket.accept().use { socket ->
                Logger.implementation.log(LogEvent.ACCEPTED_CLIENT, "{\"client\":\"${socket.localAddress}\"}", LogTag.info)
                socket.getInputStream().use {
                    while (it.available() == 0) Thread.sleep(100)
                    val lenBuffer = ByteArray(4)
                    var l = it.read(lenBuffer, 0, lenBuffer.size)
                    if (l != 4) {
                        throw Exception("Got $l bytes down when getting data length; expected a 32-bit integer (4 octets)")
                    }
                    val len = ByteBuffer.wrap(lenBuffer).int
                    val dataBuffer = ByteArray(len)
                    l = it.read(dataBuffer, 0, len)
                    if (l != len) {
                        throw Exception("Got $l bytes down; expected $len")
                    }
                    Logger.implementation.log(
                        LogEvent.GOT_BYTES,
                        """{"msg":"${ascii.decode(ByteBuffer.wrap(dataBuffer)).subSequence(0, len)}"}""", LogTag.info)
                    if (len == 0) return null
                    val m = ByteArray(len)
                    for (i in 0 until len) m[i] = dataBuffer[i]

                    return m
                }
            }
        }
    }

    fun writeString(s: String) =
        writeBytes(ascii.encode(s).array())

    private fun writeBytes(bytes: ByteArray) {
        Logger.implementation.log(
            LogEvent.AWAIT_TCP,
            "{\"info\":\"Start write... (${bytes.size}) bytes\"}", LogTag.info)
        ServerSocket(50001).use { serverSocket ->
            serverSocket.accept().use { socket ->
                Logger.implementation.log(
                    LogEvent.ACCEPTED_CLIENT,
                    "{\"client\":\"${socket.localAddress}\"}", LogTag.info)
                socket.getOutputStream().use {
                    val lenBuffer = ByteArray(4)
                    ByteBuffer.wrap(lenBuffer).putInt(bytes.size)
                    it.write(lenBuffer + bytes)
                    it.flush()
                    Logger.implementation.log(
                        LogEvent.SENT_BYTES,
                        "{\"data\":\"${bytes}\"}", LogTag.info)
                }
            }
        }
    }

    private fun getStringRaw(): String {
        var m: ByteArray? = null
        while (m == null) m = readNext()
        return ascii.decode(ByteBuffer.wrap(m)).toString()
    }

    private fun checkForHandshake(): String {
        // HACK: We use reflection to get current process handle here to ensure we compile on java 8.
        // It'll crash if it actually runs on j8, but that's fine; this code should never actually
        // run on Android devices. Reflection is obv. considerably slower, but that doesn't
        // matter b/c we only grab the PID once when we attach to a process.
        val pid = (Class.forName("java.lang.ProcessHandle").getMethod("current").invoke(null) as ProcessHandle).pid()
        val byteBuffer = ByteBuffer.allocate(Long.SIZE_BYTES + 4).putInt(IPCLayer.implementation!!.walletId).putLong(pid)
        writeBytes(ascii.encode(HANDSHAKE_MAGIC).array() + byteBuffer.array())
        println("getting reply now")
        return getStringRaw()
    }

    fun doHandshake () {
        println("start handshake")
        val handshakeReply = checkForHandshake()
        println("foo")
        if (!handshakeReply.startsWith(HANDSHAKE_REPLY_MAGIC)) {
            Logger.implementation.log(
                LogEvent.IPC_HANDSHAKE_FAIL,
                """{"handshake_reply":"$handshakeReply"}""",
                LogTag.walletError)
        }
        println("bar")
        IPCLayer.implementation!!.clientId = handshakeReply.removePrefix(HANDSHAKE_REPLY_MAGIC).toInt()
        println("handshake OK")
        writeString("OKfillerfillerfillerfillerfillerfiller")
    }

    fun readMessage(): String? {
        val bytes = readNext() ?: return null
        val json = ascii.decode(ByteBuffer.wrap(bytes)).toString()
        Logger.implementation.log(
            LogEvent.PARSED_MESSAGE,
            """{"bytes":"${Hex.encodeHexString(bytes)}", "msg":{$json}""", LogTag.info)
        return json
    }
}