package walletcore.ops

import walletcore.Core
import walletcore.constants.Keys
import walletcore.types.*

internal fun getTransaction(msg : MessageData): Response {
    val txid = msg.strings["txId"]!!

    val outgoing = when (Core.txHandler.getTransaction(txid)) {
        null -> MessageData(booleans = mutableMapOf(Keys.success to false))
        else -> {
            val tx = Core.txHandler.getTransaction(txid)
            when (tx) {
                null -> MessageData(booleans = mutableMapOf(Keys.success to false))
                else -> tx.detailsToMessageData().merge(MessageData(booleans = mutableMapOf(Keys.success to true)))
            }
        }
    }
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}