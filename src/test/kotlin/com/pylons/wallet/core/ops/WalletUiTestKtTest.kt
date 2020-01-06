package com.pylons.wallet.core.ops

import com.pylons.wallet.core.constants.Keys
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.types.MessageData
import com.pylons.wallet.core.types.Response
import com.pylons.wallet.core.types.Status

internal class WalletUiTestKtTest {
    val response = Response(MessageData(strings = mutableMapOf(Keys.INFO to "Wallet UI test OK")), Status.OK_TO_RETURN_TO_CLIENT)

    /**
     * Wallet UI test should always just return this exact response.
     */
    @Test
    fun fixedResponseMatchesExpected() {
        val actual = walletUiTest(MessageData.empty())
        assertEquals(response, actual)
    }
}