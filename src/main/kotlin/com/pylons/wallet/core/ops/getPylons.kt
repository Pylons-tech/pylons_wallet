package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun getPylons(msg: MessageData): Response {
    // Ensure we have required keys - ints["PYLONS"]
    require(msg.ints.containsKey(Keys.PYLONS)) { throw BadMessageException("getPylons", Keys.PYLONS, "Int") }
    // Generate TX and wait for commit
    val tx = Core.getPylons(msg.ints[Keys.PYLONS]!!.toLong())
    waitUntilCommitted(tx.id!!)
    // Build outgoing message
    val outgoingMsg = MessageData(
            strings = mutableMapOf(
                    Keys.TX to tx.id!!
            )
    )
    // Return to client
    return Response(outgoingMsg, Status.OK_TO_RETURN_TO_CLIENT)}

fun Core.getPylons (q : Long) : Transaction = engine.getPylons(q).submit()