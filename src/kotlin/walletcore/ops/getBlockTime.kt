package walletcore.ops

import walletcore.Core
import walletcore.types.*

internal fun getBlockTime(): Response {
    val msg = MessageData()
    msg.doubles["blockTime"] = Core.txHandler.getAverageBlockTime()
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}