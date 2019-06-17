package com.pylons.wallet.core.engine.crypto

import com.google.common.hash.Hashing
import com.squareup.moshi.Moshi
import org.apache.commons.codec.binary.Base32
import org.apache.tuweni.bytes.Bytes
import com.pylons.wallet.core.types.UserData
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.apache.tuweni.bytes.Bytes32
import org.apache.tuweni.bytes.MutableBytes
import org.apache.tuweni.bytes.MutableBytes32
import org.apache.tuweni.crypto.*
import org.apache.tuweni.crypto.sodium.SHA256Hash
import org.bouncycastle.jcajce.provider.digest.RIPEMD160
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.util.encoders.Hex
import java.security.MessageDigest


internal class CryptoCosmos () : CryptoHandler() {
    override fun getPrefix() : String = "__CRYPTO_COSMOS__"
//    private val adapter = Moshi.Builder().build().adapter<SECP256K1.KeyPair>(SECP256K1.KeyPair::class.java)
    var keyPair : SECP256K1.KeyPair? = null

    override fun importKeysFromUserData() {
        System.out.println("Importing keys")
        val bytes = Hex.decode(UserData.dataSets[getPrefix()]!!["key"]!!)
        keyPair =  SECP256K1.KeyPair.fromSecretKey(SECP256K1.SecretKey.fromBytes(Bytes32.wrap(bytes)))
        System.out.println(getCompressedPubkey(keyPair!!.publicKey()).toHexString())
    }

    override fun generateNewKeys() {
        keyPair = SECP256K1.KeyPair.random()
    }

    override fun signature(bytes: ByteArray): ByteArray = SECP256K1.sign(bytes, keyPair).bytes().toArray()

    override fun verify(bytes: ByteArray, signature : ByteArray): Boolean =
        SECP256K1.verify(bytes, SECP256K1.Signature.fromBytes(Bytes.wrap(signature)), keyPair!!.publicKey())

    override fun getEncodedPublicKey() : String = Base32().encodeToString(keyPair!!.publicKey()!!.bytesArray())

    companion object {

        fun getUncompressedPubkey (bytes : ByteArray) : SECP256K1.PublicKey {
            val SPEC = ECNamedCurveTable.getParameterSpec("secp256k1");
            val point = SPEC.curve.decodePoint(bytes)
            val x = point.xCoord.encoded
            val y = point.yCoord.encoded
            // concat 0x04, x, and y, make sure x and y has 32-bytes:
            return SECP256K1.PublicKey.fromBytes(Bytes.wrap(x + y))
        }

        fun getCompressedPubkey (key: SECP256K1.PublicKey) : Bytes {
            val ecPoint = key.asEcPoint()
            var xBytes = Bytes.wrap(ecPoint.xCoord.toBigInteger().toByteArray()).trimLeadingZeros()
            val yStr = ecPoint.yCoord.toBigInteger().toString()
            val xStr = ecPoint.xCoord.toBigInteger().toString()
            System.out.println("$xStr $yStr")
            val prefix = when (yStr > xStr) {
                true -> 0x03
                false -> 0x02
            }
            var bytes = MutableBytes.wrap(ByteArray(33))
            bytes[0] = prefix.toByte()
            xBytes.copyTo(bytes, 1)
            System.out.println(bytes.toHexString())
            return bytes
        }

        fun getAddressFromPubkey (pubkey : Bytes) : Bytes {
            val pubkey = getCompressedPubkey(SECP256K1.PublicKey.fromBytes(pubkey))
            val sha = MessageDigest.getInstance("SHA-256").digest(pubkey.toArray())
            val ripEmd = MessageDigest.getInstance("RIPEMD160").digest(sha)
            return Bytes.wrap(ripEmd)
        }

        fun getAddressFromKeyPair (keyPair : SECP256K1.KeyPair) : Bytes {
            return getAddressFromPubkey(keyPair.publicKey().bytes())
        }
    }
}