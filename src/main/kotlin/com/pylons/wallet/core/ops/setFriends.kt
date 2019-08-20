package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.*

internal fun setFriends (msg : MessageData) : Response {
    val lst = Friend.deserializeFriendsList(msg.strings[Keys.FRIENDS]!!)
    Core.setFriends(lst)
    val msgOut = MessageData(booleans = mutableMapOf(Keys.SUCCESS to true))
    return Response(msgOut, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.setFriends (lst : List<Friend>) {
    friends = lst
}