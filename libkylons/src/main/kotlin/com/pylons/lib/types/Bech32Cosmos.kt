package com.pylons.lib.types

import org.bitcoinj.core.AddressFormatException

/**
 * Implements Bech32 encoding.
 *
 * Cosmos' Bech32 code behaves slightly Wrong (tbd: confirm what the Wrong-ness is, I remember that it's a thing and
 * it's why we have this, but not what it was) which is why we have to have our own implementation.
 */
class Bech32Cosmos {
    class Bech32Data(
            val hrp: String,
            val data: ByteArray
    )

    class UnencodedBech32Data(
            val hrp: String,
            val data: ByteArray
    )


    @ExperimentalUnsignedTypes
    companion object {
        /** The Bech32 character set for encoding.  */
        private const val CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l"

        /** The Bech32 character set for decoding.  */
        private val CHARSET_REV = byteArrayOf(
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, 15, -1, 10, 17, 21, 20, 26, 30, 7, 5, -1, -1, -1, -1, -1,
                -1, -1, 29, -1, 24, 13, 25, 9, 8, 23, -1, 18, 22, 31, 27, 19, -1, 1, 0, 3, 16, 11,
                28, 12, 14, 6, 4, 2, -1, -1, -1, -1, -1, -1, 29, -1, 24, 13, 25, 9, 8, 23, -1, 18,
                22, 31, 27, 19, -1, 1, 0, 3, 16, 11, 28, 12, 14, 6, 4, 2, -1, -1, -1, -1, -1
        )

        /** Find the polynomial with value coefficients mod the generator as 30-bit.  */
        private fun polymod(values: ByteArray): Int {
            var c = 1
            for (v_i in values) {
                val c0 = c.ushr(25) and 0xff
                c = c and 0x1ffffff shl 5 xor (v_i.toInt() and 0xff)
                if (c0 and 1 != 0) c = c xor 0x3b6a57b2
                if (c0 and 2 != 0) c = c xor 0x26508e6d
                if (c0 and 4 != 0) c = c xor 0x1ea119fa
                if (c0 and 8 != 0) c = c xor 0x3d4233dd
                if (c0 and 16 != 0) c = c xor 0x2a1462b3
            }
            return c
        }

        /** Expand a HRP for use in checksum computation.  */
        private fun expandHrp(hrp: String): ByteArray {
            val hrpLength = hrp.length
            val ret = ByteArray(hrpLength * 2 + 1)
            for (i in 0 until hrpLength) {
                val c = hrp[i].toInt() and 0x7f // Limit to standard 7-bit ASCII
                ret[i] = (c.ushr(5) and 0x07).toByte()
                ret[i + hrpLength + 1] = (c and 0x1f).toByte()
            }
            ret[hrpLength] = 0
            return ret
        }

        /** Verify a checksum.  */
        private fun verifyChecksum(hrp: String, values: ByteArray): Boolean {
            val hrpExpanded = expandHrp(hrp)
            val combined = ByteArray(hrpExpanded.size + values.size)
            hrpExpanded.copyInto(combined)
            values.copyInto(combined, destinationOffset = hrpExpanded.size)
            return polymod(combined) == 1
        }

        /** Create a checksum.  */
        private fun createChecksum(hrp: String, values: ByteArray): ByteArray {
            val hrpExpanded = expandHrp(hrp)
            val enc = ByteArray(hrpExpanded.size + values.size + 6)
            hrpExpanded.copyInto(enc)
            values.copyInto(enc, startIndex = 0, destinationOffset = hrpExpanded.size)

            val mod = polymod(enc) xor 1
            val ret = ByteArray(6)
            for (i in 0..5) {
                ret[i] = (mod.ushr(5 * (5 - i)) and 31).toByte()
            }
            return ret
        }

        /**
         * Encodes a Bech32 string.
         */
        fun encode(bech32: Bech32Data): String {
            return encode(bech32.hrp, bech32.data)
        }

        /**
         * Encodes a Bech32 string.
         */
        fun encode(humanReadablePart: String, data: ByteArray): String {
            var hrp = humanReadablePart

            check(hrp.isNotEmpty()) { "Human-readable part is too short" }
            check(hrp.length <= 83) { "Human-readable part is too long" }

            hrp = hrp.toLowerCase()
            val checksum = createChecksum(hrp, data)
            val combined = ByteArray(data.size + checksum.size)
            data.copyInto(combined)
            checksum.copyInto(combined, startIndex = 0, destinationOffset = data.size)

            val sb = StringBuilder(hrp.length + 1 + combined.size)
            sb.append(hrp)
            sb.append('1')
            for (b in combined) {
                sb.append(CHARSET[b.toInt()])
            }
            return sb.toString()
        }

        /**
         * Decodes a Bech32 string.
         */
        fun decode(str: String): Bech32Data {
            var lower = false
            var upper = false
            if (str.length < 8)
                throw AddressFormatException.InvalidDataLength("Input too short: " + str.length)
            if (str.length > 90)
                throw AddressFormatException.InvalidDataLength("Input too long: " + str.length)
            for (i in 0 until str.length) {
                val c = str[i]
                if (c.toInt() < 33 || c.toInt() > 126) throw AddressFormatException.InvalidCharacter(
                        c,
                        i
                )
                if (c in 'a'..'z') {
                    if (upper)
                        throw AddressFormatException.InvalidCharacter(c, i)
                    lower = true
                }
                if (c in 'A'..'Z') {
                    if (lower)
                        throw AddressFormatException.InvalidCharacter(c, i)
                    upper = true
                }
            }
            val pos = str.lastIndexOf('1')
            if (pos < 1) throw AddressFormatException.InvalidPrefix("Missing human-readable part")
            val dataPartLength = str.length - 1 - pos
            if (dataPartLength < 6) throw AddressFormatException.InvalidDataLength("Data part too short: $dataPartLength")
            val values = ByteArray(dataPartLength)
            for (i in 0 until dataPartLength) {
                val c = str[i + pos + 1]
                if (CHARSET_REV[c.toInt()].toInt() == -1) throw AddressFormatException.InvalidCharacter(
                        c,
                        i + pos + 1
                )
                values[i] = CHARSET_REV[c.toInt()]
            }
            val hrp = str.substring(0, pos).toLowerCase()
            if (!verifyChecksum(
                            hrp,
                            values
                    )
            ) throw AddressFormatException.InvalidChecksum()
            return Bech32Data(hrp, values.copyOfRange(0, values.size - 6))
        }

        /**
         * Converts a ByteArray where each byte encodes fromBits bits to one where each byte
         * encodes toBits bits. Ported from btcutil Go implementation; see
         * https://github.com/btcsuite/btcutil/blob/master/bech32/bech32.go
         *
         * This does a lot of conversions, which isn't terribly efficient, but we want
         * to guarantee correct behavior in the event of overflow.
         */
        @kotlin.ExperimentalUnsignedTypes
        fun convertBits (data : ByteArray, fromBits : Int, toBits : Int, pad : Boolean) : ByteArray
            = Bech32CosmosExtensions.convertBits(data, 0, data.size, fromBits, toBits, pad)
        /**
         * Tendermint bech32 helper.
         * Converts from a base64 encoded byte string to base32 encoded byte string and then to bech32
         */

        fun convertAndEncode (hrp : String, data : ByteArray) : String {
            val converted = convertBits(data, 8, 5, true)
            return encode(hrp, converted)
        }


        /**
         * Tendermint bech32 helper.
         * Decodes a bech32 encoded string and converts to base64 encoded bytes
         */
        fun decodeAndConvert (bech : String) : UnencodedBech32Data {
            val data = decode(bech)
            val converted = convertBits(data.data, 5, 8, false)
            return UnencodedBech32Data(data.hrp, converted)
        }
    }
}
