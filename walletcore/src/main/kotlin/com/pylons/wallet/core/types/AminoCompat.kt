package com.pylons.wallet.core.types

import org.bouncycastle.jcajce.provider.digest.SHA256
import org.bouncycastle.util.encoders.Hex

/**
 * Nasty little helper class containing hacky methods for dealing with weird Amino errata.
 */
class AminoCompat {
    companion object {
        const val DISAMB_LENGTH = 3

        /**
         * The number of prefix bytes Amino attaches to serialized values.
         */
        const val PREFIX_LENGTH = 4

        private fun calculateDisambBytes (goType : String) : ByteArray {
            val arr = SHA256.Digest().digest(goType.toByteArray())
            return byteArrayOf(arr[0], arr[1], arr[2])
        }
        private fun calculatePrefixBytes (goType : String) : ByteArray {
            val arr = SHA256.Digest().digest(goType.toByteArray())
            return byteArrayOf(arr[3], arr[4], arr[5], arr[6])
        }

        /**
         * Attach the given prefix bytes to a raw ByteArray.
         */
        private fun prependPrefixBytes (prefix : ByteArray, raw : ByteArray) = prefix + raw

        /**
         * Attaches prefix bytes for a golang type of AccAddress to a raw ByteArray.
         * This does not attach any prefix bytes because of Mystery Cosmos Behavior.
         * We are keeping the function here anyway b/c it _should_ be attaching
         * prefix bytes and we will need to un-break it if they should ever fix this.
         */
        fun accAddress (raw : ByteArray) = raw

        /**
         * Attaches prefix bytes for a golang type of PubKeyEd25519 to a raw ByteArray.
         */
        fun pubKeyEd25519 (raw : ByteArray) = prependPrefixBytes(Hex.decode("1624DE6420"), raw)

        /**
         * Attaches prefix bytes for a golang type of PubKeySecp256K1 to a raw ByteArray.
         */
        fun pubKeySecp256k1 (raw : ByteArray) = prependPrefixBytes(Hex.decode("EB5AE98721"), raw)

        /**
         * Removes prefix bytes from an object serialized by Amino.
         * Returns the raw, un-prefixed bytearray.
         * TODO: should this provide you with a way to retrieve the prefix bytes, or is that unnecessary complexity?
         */
        fun stripPrefixBytes (raw : ByteArray) : ByteArray {
            val output = ByteArray(raw.size - PREFIX_LENGTH)
            output.forEachIndexed {i, _ -> output[i] = raw[i + PREFIX_LENGTH] }
            return output
        }

    }
}