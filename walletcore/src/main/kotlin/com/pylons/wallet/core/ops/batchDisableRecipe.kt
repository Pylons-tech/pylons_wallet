package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Transaction.Companion.submitAll

fun Core.batchDisableRecipe (recipes : List<String>) : List<Transaction> {
    val txs = engine.disableRecipes(
            recipes = recipes
    ).toMutableList()
    return txs.submitAll()
}