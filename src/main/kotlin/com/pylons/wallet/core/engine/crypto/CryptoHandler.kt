package com.pylons.wallet.core.engine.crypto

import com.pylons.wallet.core.types.UserData

internal abstract class CryptoHandler ()  {
    abstract val prefix : String
    protected abstract fun generateNewKeys ()
    protected abstract fun importKeysFromUserData()
    abstract fun signature (bytes : ByteArray) : ByteArray
    abstract fun verify (bytes : ByteArray, signature : ByteArray) : Boolean
    abstract fun getEncodedPublicKey () : String

    init {
        if (UserData.dataSets.getValue(prefix)["keys"] == null) generateNewKeys()
        else importKeysFromUserData()
    }
}