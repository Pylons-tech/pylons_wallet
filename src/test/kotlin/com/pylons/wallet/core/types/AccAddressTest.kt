package com.pylons.wallet.core.types

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

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


}