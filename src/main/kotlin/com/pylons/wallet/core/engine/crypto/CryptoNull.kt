package com.pylons.wallet.core.engine.crypto

internal class CryptoNull : CryptoHandler() {
    override fun generateNewKeys () {
        println("CryptoNull doesn't generate keys")
    }
    override fun importKeysFromUserData() {
        println("CryptoNull doesn't generate keys")
    }
    override fun signature (bytes : ByteArray) : ByteArray = byteArrayOf()
    override fun verify (bytes : ByteArray, signature : ByteArray) : Boolean = true
    override fun getEncodedPublicKey () : String = "not_a_real_pubkey"
    override fun getPrefix () : String = "__CRYPTO_NULL__"
}