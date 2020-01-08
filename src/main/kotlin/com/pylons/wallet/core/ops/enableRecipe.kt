package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.*
import com.pylons.wallet.core.types.*
import java.lang.Exception

internal fun enableRecipe (msg: MessageData) : Response {
    checkValid(msg)
    val tx = Core.enableRecipe(
            recipe = msg.strings[Keys.RECIPE]!!
    )
    waitUntilCommitted(tx.id!!)
    val outgoingMessage = MessageData(
            strings = mutableMapOf(
                    Keys.TX to tx.id!!
            )
    )
    return Response(outgoingMessage, Status.OK_TO_RETURN_TO_CLIENT)
}

private fun checkValid (msg : MessageData) {
    require(msg.strings.containsKey(Keys.RECIPE)) { throw BadMessageException("enableRecipe", Keys.RECIPE, "String") }
}

fun Core.enableRecipe (recipe : String) : Transaction = engine.enableRecipe(recipe)