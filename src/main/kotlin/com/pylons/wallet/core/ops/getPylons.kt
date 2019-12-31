package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun getPylons(msg: MessageData): Response {
    checkValid(msg)
    val tx = Core.getPylons(msg.ints[Keys.PYLONS]!!.toLong())
    waitUntilCommitted(tx.id!!)
    val outgoingMsg = MessageData(
            strings = mutableMapOf(
                    Keys.TX to tx.id!!
            )
    )
    return Response(outgoingMsg, Status.OK_TO_RETURN_TO_CLIENT)}

private fun checkValid (msg : MessageData) {
    require(msg.ints.containsKey(Keys.PYLONS)) { throw BadMessageException("getPylons", Keys.PYLONS, "Int") }
}

fun Core.getPylons (q : Long) : Transaction = engine.getPylons(q).submit()