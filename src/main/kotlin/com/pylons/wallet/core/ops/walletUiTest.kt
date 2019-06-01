package com.pylons.wallet.core.ops

import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.types.*

internal fun walletUiTest(extraArgs: MessageData): Response {
    val msg = MessageData()
    msg.strings[Keys.info] = "Wallet UI test OK"
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}