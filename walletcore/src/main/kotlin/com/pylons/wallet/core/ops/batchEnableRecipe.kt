package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*

fun Core.batchEnableRecipe (recipes : List<String>) : List<Transaction> {
    val txs = engine.enableRecipes(
            recipes = recipes
    ).toMutableList()
    return txs
}