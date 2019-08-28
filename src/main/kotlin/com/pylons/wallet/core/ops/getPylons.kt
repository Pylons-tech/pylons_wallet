package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun getPylons(msg: MessageData): Response {
    require(msg.ints.containsKey(Keys.PYLONS)) { throw BadMessageException("getPylons", Keys.PYLONS, "Int") }
    val tx = Core.getPylons(msg.ints[Keys.PYLONS]!!)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(strings = mutableMapOf(Keys.TX to tx.id!!)), Status.OK_TO_RETURN_TO_CLIENT)}

fun Core.getPylons (q : Int) : Transaction = Core.engine.getPylons(q).submit()