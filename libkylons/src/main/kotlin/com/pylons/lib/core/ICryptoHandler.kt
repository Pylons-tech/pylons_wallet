package com.pylons.lib.core

import com.pylons.lib.types.PylonsSECP256K1

interface ICryptoHandler {
    var keyPair : PylonsSECP256K1.KeyPair?
    fun generateNewKeys ()
    fun importKeysFromUserData()
    fun signature (bytes : ByteArray) : ByteArray
    fun verify (bytes : ByteArray, signature : ByteArray) : Boolean
    fun getEncodedPublicKey () : String
    fun getPrefix () : String
}