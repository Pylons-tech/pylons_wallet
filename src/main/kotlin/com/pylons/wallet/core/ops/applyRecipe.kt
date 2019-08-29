package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.*
import com.pylons.wallet.core.types.*

internal fun applyRecipe (msg: MessageData) : Response {
    checkValid(msg)
    val preferredItemIds = msg.stringArrays[Keys.PREFERRED_ITEMS] ?: mutableListOf()
    val tx = Core.applyRecipe(msg.strings[Keys.COOKBOOK]!!, msg.strings[Keys.RECIPE]!!, preferredItemIds)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(strings = mutableMapOf(Keys.TX to tx.id!!)), Status.OK_TO_RETURN_TO_CLIENT)
}

private fun checkValid (msg : MessageData) {
    require(msg.strings.containsKey(Keys.COOKBOOK)) { throw BadMessageException("applyRecipe", Keys.COOKBOOK, "String") }
    require(msg.strings.containsKey(Keys.RECIPE)) { throw BadMessageException("applyRecipe", Keys.RECIPE, "String") }
}

fun Core.applyRecipe (cookbook : String, recipe : String, preferredItemIds : List<String>?) : Transaction =
        TODO("rejigger!!!")
        //Core.engine.applyRecipe(cookbook, recipe, preferredItemIds.orEmpty()).submit()