package tech.pylons.lib

import tech.pylons.lib.types.AminoCompat
import tech.pylons.lib.types.Bech32Cosmos
import tech.pylons.lib.types.PylonsSECP256K1
import org.apache.tuweni.bytes.Bytes
import org.apache.tuweni.bytes.MutableBytes
import org.kethereum.bip32.toKey
import org.kethereum.bip39.dirtyPhraseToMnemonicWords
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.kethereum.bip39.wordlists.WORDLIST_ENGLISH
import org.spongycastle.jce.ECNamedCurveTable
import java.security.MessageDigest

object PubKeyUtil {
    fun generateMnemonic () = dirtyPhraseToMnemonicWords(org.kethereum.bip39.generateMnemonic(128, WORDLIST_ENGLISH))

    fun getLogMsgForKeys (kp : PylonsSECP256K1.KeyPair?) = when (kp) {
        null -> """null"""
        else ->  """{"pubKey":"${getCompressedPubkey(kp.publicKey()).toHexString()}"}"""
    }

    fun generateKeyPairFromMnemonic (mnemonic: MnemonicWords) : PylonsSECP256K1.KeyPair {
        val seed = mnemonic.toSeed("")
        val key = seed.toKey("m/44'/118'/0'/0/0")
        val rawPrivKey = key.keyPair.privateKey.key
        val kp =  PylonsSECP256K1.KeyPair.fromSecretKey(PylonsSECP256K1.SecretKey.fromInteger(rawPrivKey))
        //Logger.implementation.log(LogEvent.GENERATED_NEW_KEYS, getLogMsgForKeys(kp), LogTag.info)
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

    fun getAddressString (addr : ByteArray) : String {
        return Bech32Cosmos.convertAndEncode("pylo", AminoCompat.accAddress(addr))
    }
}