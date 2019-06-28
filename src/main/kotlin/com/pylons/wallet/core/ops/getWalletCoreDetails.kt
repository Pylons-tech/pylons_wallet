package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*

internal fun getWalletCoreDetails(): Response {
    val msg = MessageData()
    val info = Core.getWalletCoreDetails()
    msg.ints["coreVersionMajor"] = info.versionMajor
    msg.ints["coreVersionMinor"] = info.versionMinor
    msg.ints["coreVersionBugfix"] = info.versionBugfix
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.getWalletCoreDetails () : WalletCoreInfo = WalletCoreInfo