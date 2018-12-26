package walletcore.ops

import walletcore.Core
import walletcore.constants.Keys
import walletcore.types.MessageData
import walletcore.types.Response
import walletcore.types.Status

internal fun wipeUserData () : Response {
    Core.tearDown()
    val msg = MessageData()
    msg.strings[Keys.info] = "userDataErased"
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}