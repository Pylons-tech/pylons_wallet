package com.pylons.wallet.core.types

import org.apache.tuweni.crypto.sodium.SHA256Hash
import org.bouncycastle.jcajce.provider.digest.SHA256
import org.bouncycastle.util.encoders.Hex
import java.nio.charset.Charset

class AminoCompat {
    companion object {
        const val DISAMB_LENGTH = 3
        const val PREFIX_LENGTH = 4

        private fun calculateDisambBytes (goType : String) : ByteArray {
            val arr = SHA256.Digest().digest(goType.toByteArray())
            return byteArrayOf(arr[0], arr[1], arr[2])
        }
        private fun calculatePrefixBytes (goType : String) : ByteArray {
            val arr = SHA256.Digest().digest(goType.toByteArray())
            return byteArrayOf(arr[3], arr[4], arr[5], arr[6])
        }

        private fun prependPrefixBytes (prefix : ByteArray, raw : ByteArray) = prefix + raw

        fun accAddress (raw : ByteArray) = prependPrefixBytes(calculatePrefixBytes("AccAddress"), raw)
        fun pubKeyEd25519 (raw : ByteArray) = prependPrefixBytes(Hex.decode("0x1624DE64"), raw)
        fun pubKeySecp256k1 (raw : ByteArray) = prependPrefixBytes(Hex.decode("0xEB5AE987"), raw)

        fun stripPrefixBytes (raw : ByteArray) : ByteArray {
            val output = ByteArray(raw.size - PREFIX_LENGTH)
            output.forEachIndexed {i, _ -> output[i] = raw[i + PREFIX_LENGTH] }
            return output
        }

    }
}