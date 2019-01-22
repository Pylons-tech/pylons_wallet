package walletcore.internal


import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import walletcore.Core
import walletcore.constants.*
import walletcore.ops.*
import walletcore.types.*

internal class SuspendedActionTest {
    @Test
    fun suspendedActionIsSet () {
        requiresArgs(Actions.walletUiTest, MessageData.empty(), null, ::walletUiTest)
        assertNotNull(Core.suspendedAction)
    }
}