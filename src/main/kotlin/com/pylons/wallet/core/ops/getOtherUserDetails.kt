package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun getOtherUserDetails (msg : MessageData) : Response {
    val id = msg.strings[Keys.otherProfileId]
    if (id == null || id == "") throw BadMessageException("getOtherUserDetails", Keys.otherProfileId, "String")
    val prf = Core.getOtherUserDetails(id)
    val msgOut = when (prf) {
        null -> MessageData(booleans = mutableMapOf(Keys.profileExists to false))
        else -> MessageData(booleans = mutableMapOf(Keys.profileExists to true)).merge(prf.detailsToMessageData())
    }
    return Response(msgOut, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.getOtherUserDetails (id : String) : ForeignProfile? = Core.engine.getForeignBalances(id)