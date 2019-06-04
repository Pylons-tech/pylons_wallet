package com.pylons.wallet.core.engine.crypto

import com.google.common.hash.Hashing
import com.squareup.moshi.Moshi
import org.apache.commons.codec.binary.Base32
import org.apache.tuweni.bytes.Bytes
import com.pylons.wallet.core.types.UserData
import org.apache.commons.codec.digest.DigestUtils
import org.apache.tuweni.bytes.Bytes32
import org.apache.tuweni.bytes.MutableBytes
import org.apache.tuweni.bytes.MutableBytes32
import org.apache.tuweni.crypto.*
import org.apache.tuweni.crypto.sodium.SHA256Hash
import org.bouncycastle.jcajce.provider.digest.RIPEMD160
import java.security.MessageDigest



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
        private fun getComprressedPubkey (keyPair: SECP256K1.KeyPair) : Bytes {
            val ecPoint = keyPair.publicKey().asEcPoint()
            var xBytes = Bytes.wrap(ecPoint.xCoord.toBigInteger().toByteArray()).trimLeadingZeros()
            System.out.println(xBytes.toHexString())
            val yStr = ecPoint.yCoord.toBigInteger().toString()
            val xStr = ecPoint.xCoord.toBigInteger().toString()
            val prefix = when (yStr > xStr) {
                true -> 0x02
                false -> 0x03
            }
            var bytes = MutableBytes.wrap(ByteArray(33))
            bytes[0] = prefix.toByte()
            xBytes.copyTo(bytes, 1)
            System.out.println(bytes.toHexString())
            return bytes
        }

        fun getAddressFromKeyPair (keyPair : SECP256K1.KeyPair) : Bytes {
            val pubkey = getComprressedPubkey(keyPair)
            val sha = MessageDigest.getInstance("SHA-256").digest(pubkey.toArray())
            val ripEmd = MessageDigest.getInstance("RIPEMD160").digest(sha)
            return Bytes.wrap(ripEmd)
        }
    }
}