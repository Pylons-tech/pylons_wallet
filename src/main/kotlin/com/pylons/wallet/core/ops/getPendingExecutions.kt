package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.*

internal fun getPendingExecutions(): Response {
    val executions = Core.getPendingExecutions()
    val list = mutableListOf<String>()
    executions.forEach {
        list.add(klaxon.toJsonString(it))
    }
    val outgoingMsg = MessageData(
            stringArrays = mutableMapOf(Keys.EXECUTIONS to list)
    )
    return Response(outgoingMsg, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.getPendingExecutions () : List<Execution> = engine.getPendingExecutions()