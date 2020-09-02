package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*

fun Core.sendCoins (denom : String, q : Long, r : String) : Transaction = engine.sendCoins(denom, q, r).submit()