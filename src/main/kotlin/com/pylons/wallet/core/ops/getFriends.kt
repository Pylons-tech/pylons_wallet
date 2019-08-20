package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.*

internal fun getFriends () : Response {
    val str = Core.getFriends().serialize()
    val msgOut = MessageData(strings = mutableMapOf(Keys.FRIENDS to str))
    return Response(msgOut, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.getFriends () : List<Friend> {
    return Core.friends
}