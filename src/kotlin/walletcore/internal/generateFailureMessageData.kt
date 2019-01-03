package walletcore.internal

import walletcore.constants.Keys
import walletcore.types.MessageData
import walletcore.types.Response
import walletcore.types.Status

internal fun generateFailureMessageData(info: String): Response {
    val msg = MessageData()
    msg.strings[Keys.info] = info
    msg.booleans[Keys.success] = false
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}