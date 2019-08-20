package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.*

internal fun getUserDetails(): Response {
        val msg = when (Core.userProfile) {
            null -> MessageData(booleans = mutableMapOf(Keys.PROFILE_EXISTS to false))
            else -> {
                val prf = Core.getUserDetails()
                when (prf) {
                    null -> MessageData(booleans = mutableMapOf(Keys.PROFILE_EXISTS to false))
                    else -> prf.detailsToMessageData()
                }
            }
        }
        return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.getUserDetails () : Profile? = Core.engine.getOwnBalances()