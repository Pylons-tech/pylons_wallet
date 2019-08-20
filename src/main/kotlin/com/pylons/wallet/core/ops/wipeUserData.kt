package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.MessageData
import com.pylons.wallet.core.types.Response
import com.pylons.wallet.core.types.Status

internal fun wipeUserData () : Response {
    Core.wipeUserData()
    val msg = MessageData()
    msg.booleans[Keys.SUCCESS] = true
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.wipeUserData () {
    Core.tearDown()
    Core.onWipeUserData?.invoke()
}