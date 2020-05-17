package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.MessageData
import com.pylons.wallet.core.types.Response
import com.pylons.wallet.core.types.Status

internal fun checkExecution (msg: MessageData) : Response {
    checkValid(msg)
    val tx = Core.checkExecution(msg.strings[Keys.EXEC_ID]!!, msg.booleans[Keys.PAY_TO_COMPLETE]!!)
    waitUntilCommitted(tx.id!!)
    val outgoingMessage = MessageData(
            strings = mutableMapOf(
                    Keys.TX to tx.id!!
            )
    )
    // Return to client
    return Response(outgoingMessage, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.checkExecution(id : String, payForCompletion : Boolean) = engine.checkExecution(id, payForCompletion)

private fun checkValid (msg : MessageData) {
    require(msg.strings.containsKey(Keys.EXEC_ID)) { throw BadMessageException("checkExecution", Keys.EXEC_ID, "String") }
    require(msg.booleans.containsKey(Keys.PAY_TO_COMPLETE)) { throw BadMessageException("applyRecipe", Keys.PAY_TO_COMPLETE, "Boolean") }
}
