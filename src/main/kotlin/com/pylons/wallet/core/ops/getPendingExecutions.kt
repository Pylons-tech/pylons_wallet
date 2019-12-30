package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.*
import com.squareup.moshi.Moshi

internal fun getPendingExecutions(msg : MessageData): Response {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter(Execution::class.java)
    val executions = Core.getPendingExecutions()
    val list = mutableListOf<String>()
    executions.forEach {
        list.add(adapter.toJson(it))
    }
    val outgoingMsg = MessageData(
            stringArrays = mutableMapOf(Keys.EXECUTIONS to list)
    )
    return Response(outgoingMsg, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.getPendingExecutions () : Array<Execution> = engine.getPendingExecutions()