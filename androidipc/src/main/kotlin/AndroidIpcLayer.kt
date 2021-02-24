import android.app.Service
import com.pylons.wallet.core.types.klaxon
import com.pylons.wallet.ipc.IPCLayer
import com.pylons.wallet.ipc.Message
import java.nio.charset.Charset
import android.content.Intent
import android.os.IBinder

private const val HANDSHAKE_MAGIC = "__PYLONS_WALLET_SERVER"
private const val HANDSHAKE_REPLY_MAGIC = "__PYLONS_WALLET_CLIENT"

@IPCLayer.Implementation
class AndroidIpcLayer : IPCLayer(true) {
    class AndroidWalletIpcService : Service() {
        override fun onBind(p0: Intent?): IBinder? {
            TODO("Not yet implemented")
        }
    }


    companion object {
        private val ascii = Charset.forName("US-ASCII")

        private fun readNext(): ByteArray? {
            throw NotImplementedError()
        }

        private fun writeString(s: String) =
                writeBytes(ascii.encode(s).array())

        private fun writeBytes(bytes: ByteArray) {
            throw NotImplementedError()
        }

        private fun getStringRaw(): String {
            throw NotImplementedError()
        }

        private fun readMessage(): String? {
            throw NotImplementedError()
        }
    }

    override fun establishConnection() {
        println("establishing connection")
        throw NotImplementedError()
        println("connection established")
    }

    override fun checkConnectionStatus(): ConnectionState {
        return when (clientId) {
            0 -> ConnectionState.NoClient
            else -> ConnectionState.Connected
            // todo: implement a way to identify/recover from broken connection
        }
    }

    override fun connectionBroken() {
        TODO("Not yet implemented")
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