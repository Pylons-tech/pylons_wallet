package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.types.*

internal fun walletUiTest(extraArgs: MessageData): Response {
    val outgoingMsg = MessageData()
    outgoingMsg.strings[Keys.INFO] = Core.walletUiTest()
    return Response(outgoingMsg, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.walletUiTest() : String = "Wallet UI test OK"