package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.wallet.core.types.tx.msg.CreateTrade
import com.pylons.wallet.core.types.tx.recipe.*
import com.pylons.wallet.core.types.tx.trade.TradeItemInput

val createTradeSignable = CreateTrade(
        coinInputs = listOf(
                CoinInput("wood", 5)
        ),
        coinOutputs = listOf(

        ),
        itemInputs = listOf(
                TradeItemInput(
                        "cookbookid1",
                        ItemInput(
                                doubles = listOf(),
                                longs = listOf(),
                                strings = listOf(
                                        StringInputParam("Name", "Raichu")
                                ),
                                transferFee = FeeInputParam(0,0)
                        )
                )
        ),
        itemOutputs = listOf(

        ),
        extraInfo = "",
        sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"

)