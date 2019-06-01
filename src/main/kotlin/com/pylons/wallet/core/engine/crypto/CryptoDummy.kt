package com.pylons.wallet.core.engine.crypto

import com.squareup.moshi.Moshi
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.UserData

/**
 * Dummy CryptoHandler implementation.
 * Performs exactly no cryptography, but does so through the appropriate APIs.
 */
internal class CryptoDummy (userData: UserData?) : CryptoHandler (userData) {
    private val adapter = Moshi.Builder().build().adapter<Map<String, ByteArray>>(Map::class.java)
    var keys : Map<String, ByteArray>? = null
        internal set

    override fun importKeysFromUserData() {
        keys = adapter.fromJson(userData!!.dataSets.getValue(Core.engine!!.prefix)["keys"])
    }

    override fun generateNewKeys() {
        keys = mapOf()
    }

    override fun signature(bytes: ByteArray): ByteArray {
        return byteArrayOf()
    }

    override fun verify(bytes: ByteArray, signature : ByteArray): Boolean {
        return true
    }

    override fun getEncodedPublicKey() : String{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}