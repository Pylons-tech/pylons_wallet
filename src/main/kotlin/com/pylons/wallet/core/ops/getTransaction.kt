package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun getTransaction(msg : MessageData): Response {
    if (!msg.strings.containsKey("txId")) throw BadMessageException("getTransaction", "txId", "String")
    val tx = Core.getTransaction(msg.strings["txId"]!!)
    val msg = tx.detailsToMessageData().merge(MessageData(booleans = mutableMapOf(Keys.success to true)))
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.getTransaction(txHash : String): Transaction = engine.getTransaction(txHash)