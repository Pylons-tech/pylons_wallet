package walletcore.crypto

import walletcore.types.UserData

/**
 * Dummy CryptoHandler implementation.
 * Performs exactly no cryptography, but does so through the appropriate APIs.
 */
class CryptoDummy (userData: UserData) : CryptoHandler (userData.cryptoKeys) {
    override fun signature(bytes: ByteArray): ByteArray {
        return byteArrayOf()
    }

    override fun verify(bytes: ByteArray, signature : ByteArray): Boolean {
        return true
    }
}