package walletcore.crypto

import com.squareup.moshi.Moshi
import walletcore.types.UserData

/**
 * Dummy CryptoHandler implementation.
 * Performs exactly no cryptography, but does so through the appropriate APIs.
 */
class CryptoDummy (userData: UserData?) : CryptoHandler (userData) {
    private val adapter = Moshi.Builder().build().adapter<Map<String, ByteArray>>(Map::class.java)
    var keys : Map<String, ByteArray>? = null
        internal set

    override fun importKeysFromUserData() {
        keys = adapter.fromJson(userData!!.keys)
    }

    override fun generateNewKeys() {
        keys = mapOf()
    }

    override fun signature(bytes: ByteArray): ByteArray {
        return byteArrayOf()
    }

    override fun verify(bytes: ByteArray, signature : ByteArray): Boolean {
        return true
    }
}