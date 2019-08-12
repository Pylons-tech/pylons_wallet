package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun sendPylons(msg: MessageData): Response {
    if (msg.ints[Keys.pylons] == null) throw BadMessageException("getPylons", Keys.pylons, "Int")
    if (msg.strings["requester"] == null) throw  BadMessageException("sendPylons", "requester", "String")
    val tx = Core.sendPylons(msg.ints[Keys.pylons]!!, msg.strings["requester"]!!)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(strings = mutableMapOf(Keys.tx to tx.id!!)), Status.OK_TO_RETURN_TO_CLIENT)}

fun Core.sendPylons (q : Int, r : String) : Transaction = Core.engine.sendPylons(q, r).submit()