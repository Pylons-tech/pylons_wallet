package com.pylons.wallet.core.ops

import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.types.*

internal fun walletServiceTest(): Response {
    val msg = MessageData()
    msg.strings[Keys.info] = "Wallet service test OK"
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}