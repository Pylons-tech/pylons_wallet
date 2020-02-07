package com.pylons.wallet.core.types

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.security.KeyPairGenerator

internal class AccAddressTest {
    val invalidStrs = arrayOf(
            "hello, world!",
            "0xAA",
            "AAA",
            AccAddress.bech32PrefixAccAddr + "AB0C",
            AccAddress.bech32PrefixAccPub + "1234")

    @Test
    fun emptyAddresses () {
        assertEquals(AccAddress().toString(), "")

        val accAddr = assertDoesNotThrow(
                "Threw exception calling accAddressFromBech32 w/ empty string") {
            AccAddress.accAddressFromBech32("")
        }
        assertTrue(accAddr.empty())
    }

    @Test
    fun randBech32PubkeyConsistency() {
        for (i in 0..1000) {
            val pub = KeyPairGenerator.getInstance("Ed25519").genKeyPair().public
            val bech32AccPub = assertDoesNotThrow("Threw exception getting bech32AccPub") {
                AccAddress.bech32ifyAccPubEd25519(pub.encoded)
            }
            val accPub = assertDoesNotThrow("Threw exception getting accPub") {
                AccAddress.getAccPubKeyBech32Ed25519(bech32AccPub)
            }
        }
    }

    


}