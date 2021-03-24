package com.pylons.wallet.core.engine.crypto

import com.pylons.lib.core.ICryptoHandler
import com.pylons.lib.types.PylonsSECP256K1
import com.pylons.wallet.core.Core

internal class CryptoNull(val core : Core) : ICryptoHandler {
    override var keyPair: PylonsSECP256K1.KeyPair?
        get() = TODO("Not yet implemented")
        set(value) {}

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