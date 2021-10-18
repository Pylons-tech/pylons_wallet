package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.lib.types.tx.Coin
import tech.pylons.wallet.core.engine.TxPylonsDevEngine
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.lib.types.tx.trade.ItemRef

fun emitCreateTrade (engine : TxPylonsDevEngine, item : ItemRef, creator : String) = engine.createTrade(
        creator = creator,
        coinInputs = listOf(CoinInput(
                listOf(Coin("upylon",10))
        )),
        itemInputs = listOf(),
        coinOutputs = listOf(),
        itemOutputs = listOf(item),
        extraInfo = "asdfghjkl;asdfghjkl;asdfghjkl;"
)