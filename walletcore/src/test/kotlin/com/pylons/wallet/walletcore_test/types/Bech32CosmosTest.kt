package com.pylons.wallet.walletcore_test.types

import com.pylons.wallet.core.types.Bech32Cosmos
import org.junit.jupiter.api.Test
import org.bouncycastle.jcajce.provider.digest.SHA256

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.*

class  Bech32CosmosTest {
    /**
     * Port from btcutil bech32_test.go
     * (https://github.com/btcsuite/btcutil/blob/master/bech32/bech32_test.go)
     */
    @Test
    fun bech32 () {
        data class TestConfig (val str : String, val valid : Boolean)
        val tests = arrayOf(
                TestConfig("A12UEL5L", true),
                TestConfig("an83characterlonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio1tt5tgs", true),
                TestConfig("abcdef1qpzry9x8gf2tvdw0s3jn54khce6mua7lmqqqxw", true),
                TestConfig("11qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqc8247j", true),
                TestConfig("split1checkupstagehandshakeupstreamerranterredcaperred2y9e3w", true),
                TestConfig("split1checkupstagehandshakeupstreamerranterredcaperred2y9e2w", false),                                // invalid checksum
                TestConfig("s lit1checkupstagehandshakeupstreamerranterredcaperredp8hs2p", false),                                // invalid character (space) in hrp
                TestConfig("spl" + CharArray(127).toString() + "t1checkupstagehandshakeupstreamerranterredcaperred2y9e3w", false),              // invalid character (DEL) in hrp
                TestConfig("split1cheo2y9e2w", false),                                                                            // invalid character (o) in data part
                TestConfig("split1a2y9w", false),                                                                                 // too short data part
                TestConfig("1checkupstagehandshakeupstreamerranterredcaperred2y9e3w", false),                                     // empty hrp
                TestConfig("11qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqsqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqc8247j", false) // too long
        )

        for (test in tests) {
            val str = test.str
            if (!test.valid) {
                // Invalid string decoding should result in error.
                assertThrows<Exception>("Expected decoding to fail for invalid string $str") {
                    Bech32Cosmos.decode(str)
                }
                continue
            }

            // Valid string decoding should result in no error.
            val data = assertDoesNotThrow("Expected string to be valid bech32: $str") {
                Bech32Cosmos.decode(str)
            }

            // Check that it encodes to the same string
            val encoded = assertDoesNotThrow("Encoding failed: $str") {
                Bech32Cosmos.encode(data.hrp, data.data)
            }
            assertEquals(str.toLowerCase(), encoded, "Expected data to encode to $str but got $encoded")

            // Flip a bit in the string and make sure it is caught.
            val pos = str.lastIndexOf("1")
            val bits = BitSet.valueOf(byteArrayOf(str[pos + 1].toByte()))
            bits.flip(0)
            val flippedChar = bits.toByteArray()[0].toChar()
            val flipped = str.substring(0, pos + 1) + flippedChar + str.substring(pos + 2)
            assertThrows<Exception>("Expected decoding to fail") {
                Bech32Cosmos.decode(flipped)
            }
        }
    }

    /**
     * Port from tendermint bech32_test.go
     * (https://github.com/tendermint/tendermint/blob/master/libs/bech32/bech32_test.go)
     */
    @Test
    fun tendermintHelpers() {
        val sum = SHA256.Digest().digest("hello world\n".toByteArray(Charsets.US_ASCII))
        val bech = assertDoesNotThrow("Failed convert-and-encode operation") {
            Bech32Cosmos.convertAndEncode("shasum", sum)
        }
        val data = assertDoesNotThrow(("Failed decode-and-convert operation")) {
            Bech32Cosmos.decodeAndConvert(bech)
        }
        assertEquals("shasum", data.hrp, "Invalid hrp")
        assertArrayEquals(sum, data.data, "Invalid decode")
    }
}
