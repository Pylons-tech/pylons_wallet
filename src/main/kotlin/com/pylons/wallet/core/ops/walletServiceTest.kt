package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.types.*

internal fun walletServiceTest(): Response {
    val outgoingMsg = MessageData()
    outgoingMsg.strings[Keys.INFO] = Core.walletServiceTest()
    return Response(outgoingMsg, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.walletServiceTest(): String = "Wallet service test OK"