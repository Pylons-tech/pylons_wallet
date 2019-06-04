package com.pylons.wallet.core.engine.crypto

import com.squareup.moshi.Moshi
import org.apache.commons.codec.binary.Base32
import org.apache.tuweni.bytes.Bytes
import com.pylons.wallet.core.types.UserData
import org.apache.tuweni.bytes.Bytes32
import org.apache.tuweni.crypto.*

internal class CryptoCosmos () : CryptoHandler() {
    override fun getPrefix() : String = "__CRYPTO_COSMOS__"
    private val adapter = Moshi.Builder().build().adapter<SECP256K1.KeyPair>(SECP256K1.KeyPair::class.java)
    var keyPair : SECP256K1.KeyPair? = null

    override fun importKeysFromUserData() {
        //keyPair = adapter.fromJson(userData!!.keys)
    }

    override fun generateNewKeys() {
        keyPair = SECP256K1.KeyPair.random()
    }

    override fun signature(bytes: ByteArray): ByteArray = SECP256K1.sign(bytes, keyPair).bytes().toArray()

    override fun verify(bytes: ByteArray, signature : ByteArray): Boolean =
        SECP256K1.verify(bytes, SECP256K1.Signature.fromBytes(Bytes.wrap(signature)), keyPair!!.publicKey())

    override fun getEncodedPublicKey() : String = Base32().encodeToString(keyPair!!.publicKey()!!.bytesArray())

    companion object {
        fun getAddressFromKeyPair (keyPair : SECP256K1.KeyPair) : Bytes32 {
            throw NotImplementedError()
        }
    }
}