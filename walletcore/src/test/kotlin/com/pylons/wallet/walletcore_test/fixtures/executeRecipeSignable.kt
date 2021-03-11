package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.lib.types.types.tx.msg.ExecuteRecipe

val executeRecipeSignable = ExecuteRecipe(
        recipeId = "id0001",
        itemIds = listOf("alpha", "beta", "gamma"),
        sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"
)