package walletcore.ops

import walletcore.constants.*
import walletcore.types.*

internal fun walletUiTest(args: MessageData): Response {
    val msg = MessageData()
    msg.strings[Keys.info] = "Wallet UI test OK"
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}