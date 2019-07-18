package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.*
import com.pylons.wallet.core.types.*

internal fun applyRecipe (msg: MessageData) : Response {
    checkValid(msg)
    val preferredItemIds = msg.stringArrays[Keys.preferredItemIds] ?: mutableListOf()
    val txHash = Core.applyRecipe(msg.strings[Keys.cookbook]!!, msg.strings[Keys.recipe]!!, preferredItemIds)
    waitUntilCommitted(txHash)
    return Response(MessageData(strings = mutableMapOf(Keys.tx to txHash)), Status.OK_TO_RETURN_TO_CLIENT)
}

private fun checkValid (msg : MessageData) {
    if (!msg.strings.containsKey(Keys.cookbook)) throw BadMessageException("applyRecipe", Keys.cookbook, "String")
    if (!msg.strings.containsKey(Keys.recipe)) throw BadMessageException("applyRecipe", Keys.recipe, "String")
}

fun Core.applyRecipe (cookbook : String, recipe : String, preferredItemIds : List<String>?) : String =
        Core.engine.applyRecipe(cookbook, recipe, preferredItemIds.orEmpty())