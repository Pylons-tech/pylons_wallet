package com.pylons.wallet.core.internal

import com.pylons.wallet.core.Logger
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.constants.LogTag
import com.pylons.wallet.core.types.MessageData
import com.pylons.wallet.core.types.Response
import com.pylons.wallet.core.types.Status

internal fun generateFailureMessageData(info: String): Response {
    val msg = MessageData()
    msg.strings[Keys.info] = info
    msg.booleans[Keys.success] = false
    Logger.implementation.log(info, LogTag.info)
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}