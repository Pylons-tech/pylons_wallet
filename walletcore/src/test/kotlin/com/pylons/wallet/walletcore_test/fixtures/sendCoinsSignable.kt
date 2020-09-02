package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.wallet.core.types.Coin
import com.pylons.wallet.core.types.tx.msg.CreateRecipe
import com.pylons.wallet.core.types.tx.msg.FulfillTrade
import com.pylons.wallet.core.types.tx.msg.SendCoins
import com.pylons.wallet.core.types.tx.recipe.*

val sendCoinsSignable = SendCoins(
        amount = listOf(
                Coin("pylon", 500)
        ),
        receiver = "",
        sender = ""
)