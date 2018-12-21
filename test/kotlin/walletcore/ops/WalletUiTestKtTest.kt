package walletcore.ops

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import walletcore.types.MessageData
import walletcore.types.Response
import walletcore.types.Status

internal class WalletUiTestKtTest {
    val response = Response(MessageData(strings = mutableMapOf("info" to "Wallet UI test OK")), Status.OK_TO_RETURN_TO_CLIENT)

    /**
     * Wallet UI test should always just return this exact response.
     */
    @Test
    fun fixedResponseMatchesExpected() {
        val actual = walletUiTest(MessageData.empty())
        assertEquals(response, actual)
    }
}