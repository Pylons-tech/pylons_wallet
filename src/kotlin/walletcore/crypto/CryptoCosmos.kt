package walletcore.crypto

import walletcore.types.UserData

/**
 * Dummy CryptoHandler implementation.
 * Performs exactly no cryptography, but does so through the appropriate APIs.
 */
class CryptoCosmos (userData: UserData?) : CryptoHandler () {
    init {
        keys = userData?.cryptoKeys ?: generateNewKeys()
    }

    override fun generateNewKeys(): Map<String, ByteArray> {
        TweetNaCl.
        return mapOf()
    }

    override fun signature(bytes: ByteArray): ByteArray {
        return byteArrayOf()
    }

    override fun verify(bytes: ByteArray, signature : ByteArray): Boolean {
        return true
    }
}