package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.wallet.core.types.tx.msg.CreateRecipe
import com.pylons.wallet.core.types.tx.msg.FulfillTrade
import com.pylons.wallet.core.types.tx.recipe.*

val fulfillTradeSignable = FulfillTrade(
        tradeId = "",
        sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
        itemIds = listOf()
)