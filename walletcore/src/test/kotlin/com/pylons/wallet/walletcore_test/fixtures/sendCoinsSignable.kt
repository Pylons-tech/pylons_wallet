package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.wallet.core.types.Coin
import com.pylons.wallet.core.types.tx.msg.CreateRecipe
import com.pylons.wallet.core.types.tx.msg.FulfillTrade
import com.pylons.wallet.core.types.tx.msg.SendCoins
import com.pylons.wallet.core.types.tx.recipe.*

val sendCoinsSignable = SendCoins(
        amount = listOf(
                Coin("loudcoin", 10),
                Coin("pylon", 10)
        ),
        receiver = "cosmos13rkt5rzf4gz8dvmwxxxn2kqy6p94hkpgluh8dj",
        sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"
)