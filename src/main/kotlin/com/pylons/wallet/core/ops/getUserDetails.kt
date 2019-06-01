package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.*

internal fun getUserDetails(): Response {
        val msg = when (Core.userProfile) {
            null -> MessageData(booleans = mutableMapOf(Keys.profileExists to false))
            else -> {
                val prf = Core.engine.getOwnBalances()
                when (prf) {
                    null -> MessageData(booleans = mutableMapOf(Keys.profileExists to false))
                    else -> prf.detailsToMessageData()
                }
            }
        }
        return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}