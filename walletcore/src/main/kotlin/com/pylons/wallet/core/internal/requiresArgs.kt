package com.pylons.wallet.core.internal

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.logging.LogEvent
import com.pylons.wallet.core.logging.Logger
import com.pylons.wallet.core.logging.LogTag
import com.pylons.wallet.core.types.*

/**
 * If an operation is called without additional arguments, and it requires
 * additional arguments, it generates a Response object which signals to
 * the wallet application that the operation in question requires additional
 * arguments. In general, this means that we need to bring up the wallet UI,
 * which tends to involve the application jumping through some sort of
 * platform-specific hoops. This response's Response.msg field is null,
 * and if you try to go back to the client with that it'll crash, so
 * don't do that.
 */
@ExperimentalUnsignedTypes
internal fun requiresArgs(action : String, msg : MessageData, extraArgs: MessageData?, func: (MessageData) -> Response): Response {
    return if (extraArgs != null) func(extraArgs)
    else {
        Core.suspendedAction = action
        Core.suspendedMsg = msg
        Logger.implementation.log(LogEvent.SET_SUSPENDED_ACTION, """{"msg":${klaxon.toJsonString(msg)}}""", LogTag.info)
        Response(null, Status.REQUIRE_UI_ELEVATION)
    }
}