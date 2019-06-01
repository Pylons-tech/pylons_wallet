package com.pylons.wallet.core.internal


import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.ops.*
import com.pylons.wallet.core.types.*

internal class SuspendedActionTest {
    @Test
    fun suspendedActionIsSet () {
        requiresArgs(Actions.walletUiTest, MessageData.empty(), null, ::walletUiTest)
        assertNotNull(Core.suspendedAction)
    }
}