package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.*
import com.pylons.wallet.core.types.*
import java.lang.Exception

fun Core.applyRecipe (recipe : String, cookbook : String, itemInputs : List<String>) : Transaction {
    // HACK: list recipes, then search to find ours
    val arr = engine.listRecipes()
    var r : String? = null
    arr.forEach {
        if (it.cookbookId == cookbook && it.name == recipe) {
            r = it.id
        }
    }
    if (r == null) throw Exception("Recipe $cookbook/$recipe does not exist")
    return engine.applyRecipe(r!!, itemInputs.toTypedArray())
}