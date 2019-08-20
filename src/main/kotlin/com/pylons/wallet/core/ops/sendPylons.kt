package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun sendPylons(msg: MessageData): Response {
    if (msg.ints[Keys.PYLONS] == null) throw BadMessageException("getPylons", Keys.PYLONS, "Int")
    if (msg.strings[Keys.REQUESTER] == null) throw  BadMessageException("sendPylons", Keys.REQUESTER, "String")
    val tx = Core.sendPylons(msg.ints[Keys.PYLONS]!!, msg.strings["requester"]!!)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(strings = mutableMapOf(Keys.TX to tx.id!!)), Status.OK_TO_RETURN_TO_CLIENT)}

fun Core.sendPylons (q : Int, r : String) : Transaction = Core.engine.sendPylons(q, r).submit()