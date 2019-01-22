package walletcore.internal

import walletcore.Logger
import walletcore.constants.Keys
import walletcore.constants.LogTag
import walletcore.types.MessageData
import walletcore.types.Response
import walletcore.types.Status

internal fun generateFailureMessageData(info: String): Response {
    val msg = MessageData()
    msg.strings[Keys.info] = info
    msg.booleans[Keys.success] = false
    Logger.implementation.log(info, LogTag.info)
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}