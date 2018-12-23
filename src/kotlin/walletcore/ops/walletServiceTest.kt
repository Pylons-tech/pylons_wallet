package walletcore.ops

import walletcore.types.*

internal fun walletServiceTest(): Response {
    val msg = MessageData()
    msg.strings["info"] = "Wallet service test OK"
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}