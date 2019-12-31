package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun getTransaction(msg : MessageData): Response {
    checkValid(msg)
    val tx = Core.getTransaction(msg.strings[Keys.TX]!!)
    val outgoingMsg = tx.detailsToMessageData()
    return Response(outgoingMsg, Status.OK_TO_RETURN_TO_CLIENT)
}

private fun checkValid (msg: MessageData) {
    require (msg.strings.containsKey(Keys.TX)) { throw BadMessageException("getTransaction", Keys.TX, "String") }
}

fun Core.getTransaction(txHash : String): Transaction = engine.getTransaction(txHash)