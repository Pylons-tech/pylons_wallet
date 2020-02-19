package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun sendPylons(msg: MessageData): Response {
    require (msg.longs.containsKey(Keys.PYLONS)) { throw BadMessageException("getPylons", Keys.PYLONS, "Long") }
    require (msg.strings.containsKey(Keys.ADDRESS)) { throw BadMessageException("sendPylons", Keys.ADDRESS, "String") }
    val pylons = msg.longs[Keys.PYLONS]!!
    val address = msg.strings[Keys.ADDRESS]!!
    val tx = Core.sendPylons(pylons, address)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(strings = mutableMapOf(Keys.TX to tx.id!!)), Status.OK_TO_RETURN_TO_CLIENT)}

fun Core.sendPylons (q : Long, r : String) : Transaction = engine.sendPylons(q, r).submit()