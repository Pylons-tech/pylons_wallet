package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.msg.CreateTrade
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.lib.types.tx.trade.ItemRef
import tech.pylons.lib.types.tx.trade.TradeItemInput

val createTradeSignable = CreateTrade(
        CoinInputs = listOf(
                CoinInput(listOf(Coin("wood", 5)))
        ),
        CoinOutputs = listOf(

        ),
        ItemInputs = listOf(
                ItemInput(
                        id = "",
                        conditions = ConditionList(listOf(), listOf(), listOf()),
                        doubles = listOf(),
                        longs = listOf(),
                        strings = listOf(
                                StringInputParam("Name", "Raichu")
                        )
                )
        ),
        ItemOutputs = listOf(

        ),
        ExtraInfo = "",
        Creator = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"
)