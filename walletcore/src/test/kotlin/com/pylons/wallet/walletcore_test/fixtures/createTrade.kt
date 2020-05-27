package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.types.tx.item.Item
import com.pylons.wallet.core.types.tx.recipe.*
import java.time.Instant

fun emitCreateTrade (engine : TxPylonsDevEngine, item : Item, sender : String) = engine.createTrade(
        coinInputs = listOf(CoinInput(
                coin = "pylon",
                count = 1
        )),
        itemInputs = listOf(),
        coinOutputs = listOf(),
        itemOutputs = listOf(item),
        extraInfo = "asdfghjkl;asdfghjkl;asdfghjkl;"
)