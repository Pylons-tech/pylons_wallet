package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.*

/**
 * Extracts requires arguments from the supplied MessageData to call engine methods.
 * Resolves action type GET_PROFILE.
 * Returns a Response containing profile data when done.
 */
internal fun getProfile(msg : MessageData): Response {
        val outgoingMessage = when (val address = msg.strings[Keys.ADDRESS]) {
            null -> when (Core.userProfile) {
                null -> MessageData()
                else -> {
                    when (val userProfile = Core.getProfile()) {
                        null -> MessageData()
                        else -> userProfile.detailsToMessageData()
                    }
                }
            }
            else -> when (val foreignProfile = Core.engine.getForeignBalances(address)) {
                null -> MessageData()
                else -> foreignProfile.detailsToMessageData()
            }
        }
        return Response(outgoingMessage, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.getProfile () : Profile? = engine.getOwnBalances()