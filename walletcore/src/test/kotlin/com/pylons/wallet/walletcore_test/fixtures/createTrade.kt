package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.lib.types.types.tx.item.Item
import com.pylons.lib.types.types.tx.recipe.*

fun emitCreateTrade (engine : TxPylonsDevEngine, item : Item, sender : String) = engine.createTrade(
        coinInputs = listOf(CoinInput(
                coin = "pylon",
                count = 10
        )),
        itemInputs = listOf(),
        coinOutputs = listOf(),
        itemOutputs = listOf(item),
        extraInfo = "asdfghjkl;asdfghjkl;asdfghjkl;"
)