package walletcore.crypto

abstract class CryptoHandler  {
    var keys : Map<String, ByteArray>? = null
        internal set

    fun newKeys () {
        keys = generateNewKeys()
    }

    protected abstract fun generateNewKeys () : Map<String, ByteArray>
    abstract fun signature (bytes : ByteArray) : ByteArray
    abstract fun verify (bytes : ByteArray, signature : ByteArray) : Boolean
}