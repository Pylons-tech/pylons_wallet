package com.pylons.wallet.core.ops

import com.pylons.wallet.core.types.*

internal fun getWalletCoreDetails(): Response {
    val msg = MessageData()
    msg.ints["coreVersionMajor"] = 0
    msg.ints["coreVersionMinor"] = 1
    msg.ints["coreVersionBugfix"] = 0
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}