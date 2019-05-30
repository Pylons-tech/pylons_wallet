package walletcore.crypto

import walletcore.types.UserData

abstract class CryptoHandler (val userData: UserData?)  {
    protected abstract fun generateNewKeys ()
    protected abstract fun importKeysFromUserData()
    abstract fun signature (bytes : ByteArray) : ByteArray
    abstract fun verify (bytes : ByteArray, signature : ByteArray) : Boolean

    init {
        if (userData?.keys == null) generateNewKeys()
        else importKeysFromUserData()
    }
}