package walletcore.crypto

import walletcore.types.UserData

/**
 * Dummy CryptoHandler implementation.
 * Performs exactly no cryptography, but does so through the appropriate APIs.
 */
class CryptoDummy (userData: UserData?) : CryptoHandler () {
    init {
        keys = userData?.cryptoKeys
    }

    override fun generateNewKeys(): Map<String, ByteArray> {
        return mapOf()
    }

    override fun signature(bytes: ByteArray): ByteArray {
        return byteArrayOf()
    }

    override fun verify(bytes: ByteArray, signature : ByteArray): Boolean {
        return true
    }
}