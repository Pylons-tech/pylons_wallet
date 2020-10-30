package com.pylons.wallet.core.engine.crypto

import com.pylons.wallet.core.Core

abstract class CryptoHandler(val core : Core) {
     abstract fun generateNewKeys ()
    abstract fun importKeysFromUserData()
    abstract fun signature (bytes : ByteArray) : ByteArray
    abstract fun verify (bytes : ByteArray, signature : ByteArray) : Boolean
    abstract fun getEncodedPublicKey () : String
    abstract fun getPrefix () : String
}