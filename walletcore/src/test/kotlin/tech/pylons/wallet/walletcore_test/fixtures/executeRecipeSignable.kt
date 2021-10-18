package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.lib.types.tx.msg.ExecuteRecipe

val executeRecipeSignable = ExecuteRecipe(
        Creator = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
        RecipeID = "id0001",
        CookbookID = "CookbookID1",
        ItemIDs = listOf("alpha", "beta", "gamma"),
        CoinInputsIndex = 0
)