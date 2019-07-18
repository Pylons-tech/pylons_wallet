package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun getPylons(msg: MessageData): Response {
    if (msg.ints[Keys.pylons] == null) throw BadMessageException("getPylons", Keys.pylons, "Int")
    val txHash = Core.engine.getPylons(msg.ints[Keys.pylons]!!)
    waitUntilCommitted(txHash)
    return Response(MessageData(strings = mutableMapOf(Keys.tx to txHash)), Status.OK_TO_RETURN_TO_CLIENT)}

fun Core.getPylons (q : Int) : String = Core.engine.getPylons(q)