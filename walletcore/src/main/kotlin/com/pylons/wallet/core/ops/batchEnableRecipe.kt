package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.lib.types.types.*
import com.pylons.lib.types.types.Transaction.Companion.submitAll

fun Core.batchEnableRecipe (recipes : List<String>) : List<Transaction> {
    val txs = engine.enableRecipes(
            recipes = recipes
    ).toMutableList()
    return txs.submitAll()
}