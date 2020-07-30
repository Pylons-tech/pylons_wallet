package com.pylons.wallet.core.engine.crypto

import com.pylons.wallet.core.logging.LogEvent
import com.pylons.wallet.core.logging.LogTag
import com.pylons.wallet.core.logging.Logger
import com.pylons.wallet.core.types.PylonsSECP256K1
import org.apache.commons.codec.binary.Base32
import org.apache.tuweni.bytes.Bytes
import com.pylons.wallet.core.types.UserData
import org.apache.tuweni.bytes.Bytes32
import org.apache.tuweni.bytes.MutableBytes
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.util.encoders.Hex
import java.security.MessageDigest
import org.kethereum.bip32.*
import org.kethereum.bip39.*
import org.kethereum.bip39.model.MnemonicWords

import org.kethereum.bip39.wordlists.WORDLIST_ENGLISH

class CryptoCosmos : CryptoHandler() {
    override fun getPrefix() : String = "__CRYPTO_COSMOS__"
    var keyPair : PylonsSECP256K1.KeyPair? = null

    override fun importKeysFromUserData() {
        val bytes = Hex.decode(UserData.dataSets[getPrefix()]!!["key"]!!.removePrefix("0x"))
        keyPair =  PylonsSECP256K1.KeyPair.fromSecretKey(PylonsSECP256K1.SecretKey.fromBytes(Bytes32.wrap(bytes)))
        Logger.implementation.log(LogEvent.IMPORTED_KEYS, getLogMsgForKeys(keyPair), LogTag.info)
    }

    override fun generateNewKeys() {
        keyPair = generateKeyPairFromMnemonic(generateMnemonic())
    }

    override fun signature(bytes: ByteArray): ByteArray = PylonsSECP256K1.sign(bytes, keyPair!!).bytes().toArray().slice(0 until 64).toByteArray()

    override fun verify(bytes: ByteArray, signature : ByteArray): Boolean =
            PylonsSECP256K1.verify(bytes, PylonsSECP256K1.Signature.fromBytes(Bytes.wrap(signature)), keyPair!!.publicKey())

    override fun getEncodedPublicKey() : String = Base32().encodeToString(keyPair!!.publicKey()!!.bytesArray())

    companion object {

        fun generateMnemonic () = dirtyPhraseToMnemonicWords(generateMnemonic(128, WORDLIST_ENGLISH))

        private fun getLogMsgForKeys (kp : PylonsSECP256K1.KeyPair?) = when (kp) {
            null -> """null"""
            else ->  """{"pubKey":"${getCompressedPubkey(kp.publicKey()).toHexString()}"}"""
        }

        fun generateKeyPairFromMnemonic (mnemonic: MnemonicWords) : PylonsSECP256K1.KeyPair {
            val seed = mnemonic.toSeed("")
            val key = seed.toKey("m/44'/118'/0'/0/0")
            val rawPrivKey = key.keyPair.privateKey.key
            val kp =  PylonsSECP256K1.KeyPair.fromSecretKey(PylonsSECP256K1.SecretKey.fromInteger(rawPrivKey))
            Logger.implementation.log(LogEvent.GENERATED_NEW_KEYS, getLogMsgForKeys(kp), LogTag.info)
            return kp
        }

        fun getUncompressedPubkey (bytes : ByteArray) : PylonsSECP256K1.PublicKey {
            val spec = ECNamedCurveTable.getParameterSpec("secp256k1")
            val point = spec.curve.decodePoint(bytes)
            val x = point.xCoord.encoded
            val y = point.yCoord.encoded
            // concat 0x04, x, and y, make sure x and y has 32-bytes:
            return PylonsSECP256K1.PublicKey.fromBytes(Bytes.wrap(x + y))
        }

        fun getCompressedPubkey (key: PylonsSECP256K1.PublicKey) : Bytes {
            val ecPoint = key.asEcPoint()
            val xBytes = Bytes.wrap(ecPoint.xCoord.toBigInteger().toByteArray()).trimLeadingZeros()
            val yStr = ecPoint.yCoord.toBigInteger().toString()
            val xStr = ecPoint.xCoord.toBigInteger().toString()
            val prefix = when (ecPoint.yCoord.toBigInteger() % 2.toBigInteger() == 0.toBigInteger()) {
                true -> 0x02
                false -> 0x03
            }
            val bytes = MutableBytes.wrap(ByteArray(33))
            bytes[0] = prefix.toByte()
            xBytes.copyTo(bytes, 1)
            return bytes
        }

        fun getAddressFromPubkey (key: PylonsSECP256K1.PublicKey) : Bytes {
            val pubkey = getCompressedPubkey(key)
            val sha = MessageDigest.getInstance("SHA-256").digest(pubkey.toArray())
            val ripEmd = MessageDigest.getInstance("RIPEMD160").digest(sha)
            return Bytes.wrap(ripEmd)
        }

        fun getAddressFromKeyPair (keyPair : PylonsSECP256K1.KeyPair) : Bytes {
            return getAddressFromPubkey(keyPair.publicKey())
        }
    }
}