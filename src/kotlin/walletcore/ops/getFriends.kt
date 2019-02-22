package walletcore.ops

import walletcore.Core
import walletcore.constants.Keys
import walletcore.types.*

internal fun getFriends () : Response {
    val str = Core.friends.serialize()
    val msgOut = MessageData(strings = mutableMapOf(Keys.friends to str))
    return Response(msgOut, Status.OK_TO_RETURN_TO_CLIENT)
}