package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.generateErrorMessageData
import com.pylons.wallet.core.types.*

internal fun getOtherUserDetails (msg : MessageData) : Response {
    if (!msg.strings.containsKey(Keys.otherProfileId)) return generateErrorMessageData(Error.MALFORMED_TX, "Did not provide an ID for remote profile.")
    val prf = Core.engine.getForeignBalances(msg.strings[Keys.otherProfileId]!!)
    val msgOut = when (prf) {
        null -> MessageData(booleans = mutableMapOf(Keys.profileExists to false))
        else -> MessageData(booleans = mutableMapOf(Keys.profileExists to true)).merge(prf.detailsToMessageData())
    }
    return Response(msgOut, Status.OK_TO_RETURN_TO_CLIENT)
}