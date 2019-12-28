package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.*

internal fun getProfile(msg : MessageData): Response {
        val m = when (val address = msg.strings[Keys.ADDRESS]) {
            null -> when (Core.userProfile) {
                null -> MessageData()
                else -> {
                    when (val prf = Core.getProfile()) {
                        null -> MessageData()
                        else -> prf.detailsToMessageData()
                    }
                }
            }
            else -> when (val f = Core.engine.getForeignBalances(address)) {
                null -> MessageData()
                else -> f.detailsToMessageData()
            }
        }
        return Response(m, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.getProfile () : Profile? = engine.getOwnBalances()