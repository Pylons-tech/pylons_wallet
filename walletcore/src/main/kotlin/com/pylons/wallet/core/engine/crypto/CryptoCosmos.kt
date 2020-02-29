package com.pylons.wallet.core.engine.crypto

import com.pylons.wallet.core.Logger
import org.apache.commons.codec.binary.Base32
import org.apache.tuweni.bytes.Bytes
import com.pylons.wallet.core.types.UserData
import org.apache.tuweni.bytes.Bytes32
import org.apache.tuweni.bytes.MutableBytes
import com.pylons.wallet.core.types.SECP256K1
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.util.encoders.Hex
import java.security.MessageDigest
import org.kethereum.bip32.*
import org.kethereum.bip39.*
import org.komputing.kbip44.*

import org.kethereum.bip39.wordlists.WORDLIST_ENGLISH

internal class CryptoCosmos : CryptoHandler() {
    override fun getPrefix() : String = "__CRYPTO_COSMOS__"
    var keyPair : SECP256K1.KeyPair? = null

    fun keysFromSeed (seed : ByteArray) {
        //SECP256K1.KeyPair.fromSecretKey()
    }

    override fun importKeysFromUserData() {
        println("Importing keys")
        val bytes = Hex.decode(UserData.dataSets[getPrefix()]!!["key"]!!.removePrefix("0x"))
        keyPair =  SECP256K1.KeyPair.fromSecretKey(SECP256K1.SecretKey.fromBytes(Bytes32.wrap(bytes)))
        println(getCompressedPubkey(keyPair!!.publicKey()).toHexString())
    }



    override fun generateNewKeys() {
        val mnemonic= dirtyPhraseToMnemonicWords(generateMnemonic(128, WORDLIST_ENGLISH))
        val seed = mnemonic.toSeed("")
        val key = seed.toKey("m/44'/118'/0'/0/0")
        val rawPrivKey = key.keyPair.privateKey.key
        keyPair = SECP256K1.KeyPair.fromSecretKey(SECP256K1.SecretKey.fromInteger(rawPrivKey))
        Logger.implementation.log("Generated new keys: \n" +
                "public key: ${Hex.toHexString(getCompressedPubkey(keyPair!!.publicKey()).toArray())}",
                "INFO")
    }

    override fun signature(bytes: ByteArray): ByteArray = SECP256K1.sign(bytes, keyPair).bytes().toArray().slice(0 until 64).toByteArray()

    override fun verify(bytes: ByteArray, signature : ByteArray): Boolean =
        SECP256K1.verify(bytes, SECP256K1.Signature.fromBytes(Bytes.wrap(signature)), keyPair!!.publicKey())

    override fun getEncodedPublicKey() : String = Base32().encodeToString(keyPair!!.publicKey()!!.bytesArray())

    companion object {

        fun getUncompressedPubkey (bytes : ByteArray) : SECP256K1.PublicKey {
            val spec = ECNamedCurveTable.getParameterSpec("secp256k1")
            val point = spec.curve.decodePoint(bytes)
            val x = point.xCoord.encoded
            val y = point.yCoord.encoded
            // concat 0x04, x, and y, make sure x and y has 32-bytes:
            return SECP256K1.PublicKey.fromBytes(Bytes.wrap(x + y))
        }

        fun getCompressedPubkey (key: SECP256K1.PublicKey) : Bytes {
            val ecPoint = key.asEcPoint()
            val xBytes = Bytes.wrap(ecPoint.xCoord.toBigInteger().toByteArray()).trimLeadingZeros()
            val yStr = ecPoint.yCoord.toBigInteger().toString()
            val xStr = ecPoint.xCoord.toBigInteger().toString()
            println("$xStr $yStr")
            val prefix = when (ecPoint.yCoord.toBigInteger() % 2.toBigInteger() == 0.toBigInteger()) {
                true -> 0x02
                false -> 0x03
            }
            println("PREFIX: $prefix")
            val bytes = MutableBytes.wrap(ByteArray(33))
            bytes[0] = prefix.toByte()
            xBytes.copyTo(bytes, 1)
            println(bytes.toHexString())
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