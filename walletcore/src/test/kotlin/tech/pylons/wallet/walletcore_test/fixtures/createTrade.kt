package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.wallet.core.engine.TxPylonsDevEngine
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.*

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