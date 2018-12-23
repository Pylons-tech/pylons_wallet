package walletcore.ops

import walletcore.types.*

internal fun getWalletCoreDetails(): Response {
    val msg = MessageData()
    msg.ints["coreVersionMajor"] = 0
    msg.ints["coreVersionMinor"] = 1
    msg.ints["coreVersionBugfix"] = 0
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}