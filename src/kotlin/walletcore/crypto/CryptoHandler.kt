package walletcore.crypto

abstract class CryptoHandler (val keys : Map<String, ByteArray>) {
    abstract fun signature (bytes : ByteArray) : ByteArray
    abstract fun verify (bytes : ByteArray, signature : ByteArray) : Boolean
}