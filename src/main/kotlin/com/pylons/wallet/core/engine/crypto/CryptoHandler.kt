package com.pylons.wallet.core.engine.crypto

import com.pylons.wallet.core.types.UserData

internal abstract class CryptoHandler {
     abstract fun generateNewKeys ()
    abstract fun importKeysFromUserData()
    abstract fun signature (bytes : ByteArray) : ByteArray
    abstract fun verify (bytes : ByteArray, signature : ByteArray) : Boolean
    abstract fun getEncodedPublicKey () : String
    abstract fun getPrefix () : String


    init {
        //if (UserData.dataSets.getValue(getPrefix())["keys"] == null) generateNewKeys()
        //else importKeysFromUserData()
    }
}