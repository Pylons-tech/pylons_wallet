//package com.pylons.wallet.coretest.internal
//
//
//import org.junit.jupiter.api.Test
//
//import org.junit.jupiter.api.Assertions.*
//import com.pylons.wallet.core.Core
//import com.pylons.wallet.core.constants.*
//import com.pylons.wallet.core.internal.requiresArgs
//import com.pylons.wallet.core.ops.*
//import com.pylons.wallet.core.types.*
//
//internal class SuspendedActionTest {
//    @Test
//    fun suspendedActionIsSet () {
//        requiresArgs(Actions.WALLET_UI_TEST, MessageData.empty(), null, ::walletUiTest)
//        assertNotNull(Core.suspendedAction)
//    }
//}