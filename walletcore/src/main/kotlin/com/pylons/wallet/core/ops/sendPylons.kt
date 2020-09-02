package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Coin.Companion.toCoinList

fun Core.sendCoins (coins : Map<String, Long>, receiver : String) : Transaction =
        engine.sendCoins(coins.toCoinList(), receiver).submit()