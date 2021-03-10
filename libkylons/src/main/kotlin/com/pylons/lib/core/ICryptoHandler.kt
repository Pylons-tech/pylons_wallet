package com.pylons.lib.core

interface ICryptoHandler {
    fun generateNewKeys ()
    fun importKeysFromUserData()
    fun signature (bytes : ByteArray) : ByteArray
    fun verify (bytes : ByteArray, signature : ByteArray) : Boolean
    fun getEncodedPublicKey () : String
    fun getPrefix () : String
}