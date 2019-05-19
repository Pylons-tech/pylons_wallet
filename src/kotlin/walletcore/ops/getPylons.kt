package walletcore.ops

import walletcore.Core
import walletcore.Logger
import walletcore.constants.Keys
import walletcore.constants.LogTag
import walletcore.types.*

internal fun getPylons(msg: MessageData): Response {
    val preferredItemIds = msg.stringArrays[Keys.preferredItemIds] ?: mutableListOf()
    val msg = when (Core.txHandler.getPylons(msg.ints[Keys.pylons]!!)) {
        null -> MessageData(booleans = mutableMapOf(Keys.success to false))
        else -> MessageData(booleans = mutableMapOf(Keys.success to true))
    }
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}