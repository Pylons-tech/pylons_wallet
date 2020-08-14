import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.logging.LogEvent
import com.pylons.wallet.core.logging.LogTag
import com.pylons.wallet.core.logging.Logger
import com.pylons.wallet.core.types.klaxon
import com.pylons.wallet.ipc.IPCLayer
import com.pylons.wallet.ipc.Message
import org.apache.commons.codec.binary.Hex
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.charset.Charset

private const val HANDSHAKE_MAGIC = "__PYLONS_WALLET_SERVER"
private const val HANDSHAKE_REPLY_MAGIC = "__PYLONS_WALLET_CLIENT"

@IPCLayer.Implementation
class IpcLayer : IPCLayer() {
    companion object {

        private var socket: ServerSocket = ServerSocket(50001)
        private var client: Socket? = null
        private val ascii = Charset.forName("US-ASCII")

        private fun readNext(): ByteArray? {
            Logger.implementation.log(LogEvent.AWAIT_MESSAGE, "", LogTag.info)
            val s = client!!.getInputStream()
            while (s.read() == -1) Thread.sleep(100)
            val buffer = ByteArray(1024 * 512) // 512kb is overkill
            val len = s.read(buffer, 0, buffer.size)
            Logger.implementation.log(LogEvent.RESOLVED_MESSAGE,
                    """{"msg":"${ascii.decode(ByteBuffer.wrap(buffer)).subSequence(0, len)}"}""", LogTag.info)
            if (len == 0) return null
            val m = ByteArray(len)
            for (i in 0 until len) m[i] = buffer[i]
            return m
        }

        private fun writeString(s: String) =
                writeBytes(ascii.encode(s).array())

        private fun writeBytes(bytes: ByteArray) {
            println("Start write... (" + bytes.size + ") bytes")
            val s = client!!.getOutputStream()
            s.write(0xFF) // meaningless, just prepending message w/ an irrelevant byte so we can use ReadByte() to check for stream end w/o losing data
            s.write(bytes, 0, bytes.size)
            s.flush()
            println("Write complete")
        }

        private fun getHandshakeReply(): String {
            var m: ByteArray? = null
            while (m == null) m = readNext()
            return ascii.decode(ByteBuffer.wrap(m)).toString()
        }

        private fun checkForHandshake(): String {
            client = socket.accept()
            val pid = ProcessHandle.current().pid()
            val byteBuffer = ByteBuffer.allocate(Long.SIZE_BYTES).putLong(pid)
            writeBytes(ascii.encode(HANDSHAKE_MAGIC).array() + byteBuffer.array())
            return getHandshakeReply()
        }

        private fun doHandshake () {
            val handshakeReply = checkForHandshake()
            if (handshakeReply != HANDSHAKE_REPLY_MAGIC) {
                Logger.implementation.log(
                        LogEvent.IPC_HANDSHAKE_FAIL,
                        """{"handshake_reply":"$handshakeReply"}""",
                        LogTag.walletError)
                closeClient()
            }
        }

        private fun readMessage(): String? {
            val bytes = readNext()?: return null
            val json = ascii.decode(ByteBuffer.wrap(bytes)).toString()
            Logger.implementation.log(LogEvent.PARSED_MESSAGE,
                    """{"bytes":"${Hex.encodeHexString(bytes)}", "msg":{$json}""", LogTag.info)
            return json
        }

        private fun closeClient() {
            client?.close()
            client = null
        }
    }

    override fun checkConnectionStatus() : ConnectionState {
        return when (client?.isConnected) {
            null -> ConnectionState.NoClient
            false -> ConnectionState.ConnectionBroken
            true -> ConnectionState.Connected
        }
    }

    override fun connectionBroken() {
        closeClient()
    }

    override fun establishConnection() {
        client = socket.accept()
        doHandshake()
    }

    override fun getNextJson(callback: (String) -> Unit) {
        callback(readMessage().orEmpty())
    }

    override fun reject(json: String) {
        writeString("""{"rejected_message":"$json"}""")
    }

    override fun submit(r: Message.Response) {
        writeString(klaxon.toJsonString(r))
    }

}