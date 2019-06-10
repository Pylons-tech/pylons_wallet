package com.pylons.wallet.core.types

import com.sun.org.apache.xpath.internal.operations.Bool
import org.bitcoinj.core.Bech32

internal class Bech32Cosmos {
    class Bech32Data (
            val data : ByteArray,
            val hrp : String
    )

    class UnencodedBech32Data (
        val data : ByteArray,
        val hrp : String
    )



    companion object {
        infix fun Byte.shl(that: Int): Byte = (this.toInt().shl(that)).toByte()
        infix fun Int.shl(that: Byte): Byte = (this.shl(that.toInt())).toByte()
        infix fun Byte.shl(that: Byte): Byte = (this.toInt().shl(that.toInt())).toByte()
        infix fun Byte.shr(that: Int): Byte = (this.toInt().shr(that)).toByte()
        infix fun Int.shr(that: Byte): Byte = (this.shr(that.toInt())).toByte()
        infix fun Byte.shr(that: Byte): Byte = (this.toInt().shr(that.toInt())).toByte()
        infix fun Byte.or(that: Int): Byte = (this.toInt().and(that)).toByte()
        infix fun Int.or(that: Byte): Byte = (this.and(that.toInt())).toByte()
        infix fun Byte.or(that: Byte): Byte = (this.toInt().and(that.toInt())).toByte()


        fun convertAndEncode (hrp : String, data : ByteArray) : String {
            val converted = convertBits(data, 8, 5, true)
            return encode(hrp, converted)
        }

        /**
         * Converts a ByteArray where each byte encodes fromBits bits to one where each byte
         * encodes toBits bits. Ported from btcutil Go implementation; see
         * https://github.com/btcsuite/btcutil/blob/master/bech32/bech32.go
         */
        fun convertBits (data : ByteArray, fromBits : Byte, toBits : Byte, pad : Boolean) : ByteArray {
            if (fromBits < 1 || fromBits > 8 || toBits < 1 || toBits > 8) {
                throw Exception("Bit groups must be between 1 and 8, but provided were fromBits:" +
                        "$fromBits and toBits: $toBits")
            }

            // Final bytes, each byte encoding toBits bits
            var regrouped : MutableList<Byte> = mutableListOf()

            // Next byte to be created
            var nextByte = 0.toByte()

            // Number of bits added to nextByte (up to goal of toBits)
            var filledBits = 0.toByte()

            data.forEach{
                // Discard unused bits
                var b = it.shr(8 - fromBits)

                // Bits remaining to extract from input data
                var remFromBits = fromBits
                while (remFromBits > 0) {
                    // Bits remaining to add to next byte
                    var remToBits = (toBits - filledBits).toByte()

                    // Number of bytes to extract next.
                    // The lesser of remToBits and remFromBits.
                    var toExtract = when (remToBits < remFromBits) {
                        true -> remToBits
                        false -> remFromBits
                    }

                    // Add the next bits to nextByte, shifting the already-added bits left.
                    nextByte = (nextByte.shl(toExtract)).or(b.shl(8 - toExtract)).toByte()

                    // Discard the bits we just extracted and get ready for next iteration.
                    b = b.shl(toExtract).toByte()
                    remFromBits = (remFromBits - toExtract).toByte()
                    filledBits = (filledBits + toExtract).toByte()

                    // If nextByte is completely filled, add it to our regrouped bytes and start on the next byte.
                    if (filledBits == toBits) {
                        regrouped.add(nextByte)
                        filledBits = 0
                        nextByte = 0
                    }
                }
            }

            // Pad an unfinished group if specified (and group is unfinished)
            if (pad && filledBits > 0) {
                nextByte = nextByte.shl((toBits - filledBits))
                regrouped.add(nextByte)
                filledBits = 0
                nextByte = 0
            }

            // Any incomplete group must be <= 4 bits, and all zeroes.
            if (filledBits > 0 && (filledBits > 4 || nextByte != 0.toByte())) {
                throw Exception("Invalid incomplete group")
            }

            return regrouped.toByteArray()
        }

        fun decode (input : String) : UnencodedBech32Data {
            var bech = input

            // The maximum allowed length for a bech32 string is 90. It must also
            // be at least 8 characters, since it needs a non-empty HRP, a
            // separator, and a 6 character checksum.
            if (bech.length < 8 || bech.length > 90) throw Exception("Invalid Bech32 string length ${bech.length}")

            // Only	ASCII characters between 33 and 126 are allowed.
            for (i in 0..bech.length) {
                if (bech[i] < 33.toChar() || bech[i] > 126.toChar()) throw Exception("invalid character in string: ${bech[i]}")
            }

            // The characters must be either all lowercase or all uppercase.
            val lower = bech.toLowerCase()
            val upper = bech.toUpperCase()
            if (bech != lower && bech != upper) throw Exception("String not all lowercase or all uppercase")
            bech = lower

            // The string is invalid if the last '1' is non-existent, the first character of the string
            // (no human-readable part), or one of the last 6 characters of the string
            // (since checksum cannot contain '1').
            val one = bech.lastIndexOf('1')
            if (one < 1 || one+7 > bech.length) throw Exception("Index of last '1' in string is $one, which is invalid")
            // The human-readable part is everything before the last '1'.
            val hrp = bech.substring(0, one)
            val data = bech.substring(one + 1)

            // Each character corresponds to the byte with value of the index in
            // 'charset'.
            val decoded = toBytes(data)

            // TODO: I'm not actually sure the array slicing here is right.
            //  We'll be porting the tests, too, so we'll see then.

            if (!bech32VerifyChecksum(hrp, decoded)) {
                val checksum = bech.substring(bech.length-6)
                val expected = toChars(bech32Checksum(hrp,
                        decoded.slice(0..decoded.size-6).toByteArray()))
                throw Exception("Checksum failed. Expected: $expected, got: $checksum")
            }

            // We exclude the last 6 bytes, which is the checksum.
            return UnencodedBech32Data(decoded.slice(0..decoded.size-6).toByteArray(), hrp)
        }

        fun decodeAndConvert (bech : String) : UnencodedBech32Data {
            val data = decode(bech)
            val converted = convertBits(data.data, 5, 8, false)
            return UnencodedBech32Data(converted, data.hrp)
        }

        fun encode (hrp : String, data : ByteArray) : String{
            throw NotImplementedError()
        }

        fun bech32VerifyChecksum(hrp : String, data : ByteArray) : Boolean {
            throw NotImplementedError()
        }

        fun bech32Checksum(hrp : String, data : ByteArray) : ByteArray {
            throw NotImplementedError()
        }

        fun toBytes (chars : String) : ByteArray {
            throw NotImplementedError()
        }

        fun toChars (data : ByteArray) : String {
            throw NotImplementedError()
        }

    }
}
