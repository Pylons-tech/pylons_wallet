package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.*
import com.pylons.wallet.core.types.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.Exception

internal fun applyRecipe (msg: MessageData) : Response {
    checkValid(msg)
    val tx = Core.applyRecipe(
            recipe = msg.strings[Keys.RECIPE]!!,
            cookbook = msg.strings[Keys.COOKBOOK]!!,
            itemInputs = msg.stringArrays[Keys.ITEM_INPUTS]!!
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
    require(msg.strings.containsKey(Keys.RECIPE)) { throw BadMessageException("applyRecipe", Keys.RECIPE, "String") }
    require(msg.strings.containsKey(Keys.COOKBOOK)) { throw BadMessageException("applyRecipe", Keys.COOKBOOK, "String") }
    require(msg.stringArrays.containsKey(Keys.ITEM_INPUTS)) { throw BadMessageException("applyRecipe", Keys.ITEM_INPUTS, "StringArray") }
}

fun Core.applyRecipe (recipe : String, cookbook : String, itemInputs : List<String>) : Transaction {
    // HACK: list recipes, then search to find ours
    val arr = engine.listRecipes()
    var r : String? = null
    arr.forEach {
        if (it.cookbook == cookbook && it.name == recipe) {
            r = it.id
        }
    }
    if (r == null) throw Exception("Recipe $cookbook/$recipe does not exist")
    return engine.applyRecipe(r!!, itemInputs.toTypedArray())
}