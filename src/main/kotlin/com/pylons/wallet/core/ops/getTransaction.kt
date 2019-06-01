package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.*

internal fun getTransaction(msg : MessageData): Response {
    val txid = msg.strings["txId"]!!

    val outgoing = when (Core.engine.getTransaction(txid)) {
        null -> MessageData(booleans = mutableMapOf(Keys.success to false))
        else -> {
            val tx = Core.engine.getTransaction(txid)
            when (tx) {
                null -> MessageData(booleans = mutableMapOf(Keys.success to false))
                else -> tx.detailsToMessageData().merge(MessageData(booleans = mutableMapOf(Keys.success to true)))
            }
        }
    }
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}