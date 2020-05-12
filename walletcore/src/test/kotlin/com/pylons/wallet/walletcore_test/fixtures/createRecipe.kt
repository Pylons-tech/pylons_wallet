package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.types.tx.recipe.CoinInput
import com.pylons.wallet.core.types.tx.recipe.EntriesList
import java.time.Instant

fun emitCreateRecipe (engine : TxPylonsDevEngine, name : String, cbId : String, sender : String) = engine.createRecipe(
        blockInterval = 0,
        coinInputs = listOf(CoinInput(
                coin = "pylon",
                count = 1
        )),
        cookbookId = cbId,
        description = "test recipe from test suite",
        entries = EntriesList(
                coinOutputs = listOf(),
                itemOutputs = listOf()
        ),
        outputs = listOf(),
        itemInputs = listOf(),
        name = name,
        sender = sender
)