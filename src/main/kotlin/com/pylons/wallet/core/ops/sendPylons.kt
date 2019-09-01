package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun sendPylons(msg: MessageData): Response {
    require (msg.ints.containsKey(Keys.PYLONS)) { throw BadMessageException("getPylons", Keys.PYLONS, "Int") }
    require (msg.strings.containsKey(Keys.REQUESTER)) { throw BadMessageException("sendPylons", Keys.REQUESTER, "String") }
    val tx = Core.sendPylons(msg.ints[Keys.PYLONS]!!.toLong(), msg.strings["requester"]!!)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(strings = mutableMapOf(Keys.TX to tx.id!!)), Status.OK_TO_RETURN_TO_CLIENT)}

fun Core.sendPylons (q : Long, r : String) : Transaction = Core.engine.sendPylons(q, r).submit()