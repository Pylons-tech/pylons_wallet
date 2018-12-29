package walletcore.ops

import walletcore.Core
import walletcore.constants.Keys
import walletcore.types.*

internal fun getUserDetails(): Response {
        val msg = when (Core.userProfile) {
            null -> MessageData(booleans = mutableMapOf(Keys.profileExists to false))
            else -> {
                val prf = Core.txHandler.getOwnBalances()
                when (prf) {
                    null -> MessageData(booleans = mutableMapOf(Keys.profileExists to false))
                    else -> prf.detailsToMessageData()
                }
            }
        }
        return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}