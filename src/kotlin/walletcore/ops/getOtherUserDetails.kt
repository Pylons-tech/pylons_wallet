package walletcore.ops

import walletcore.Core
import walletcore.constants.Keys
import walletcore.internal.generateErrorMessageData
import walletcore.types.*

internal fun getOtherUserDetails (msg : MessageData) : Response {
    if (!msg.strings.containsKey(Keys.otherProfileId)) return generateErrorMessageData(Error.MALFORMED_TX, "Did not provide an ID for remote profile.")
    val prf = Core.txHandler.getForeignBalances(Keys.otherProfileId)
    val msgOut = when (prf) {
        null -> MessageData(booleans = mutableMapOf(Keys.profileExists to false))
        else -> MessageData(booleans = mutableMapOf(Keys.profileExists to true))
    }
    return Response(msgOut, Status.OK_TO_RETURN_TO_CLIENT)
}