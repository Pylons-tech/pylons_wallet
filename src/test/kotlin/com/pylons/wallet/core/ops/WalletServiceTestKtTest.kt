package com.pylons.wallet.core.ops

import com.pylons.wallet.core.constants.Keys
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.types.MessageData
import com.pylons.wallet.core.types.Response
import com.pylons.wallet.core.types.Status
import com.pylons.wallet.core.types.klaxon

internal class WalletServiceTestKtTest {
    val response = Response(MessageData(strings = mutableMapOf(Keys.INFO to "Wallet service test OK")), Status.OK_TO_RETURN_TO_CLIENT)

    /**
     * Wallet service test should always just return this exact response.
     */
    @Test
    fun fixedResponseMatchesExpected() {
        val actual = walletServiceTest()
        assertEquals(klaxon.toJsonString(response), klaxon.toJsonString(actual))
    }
}