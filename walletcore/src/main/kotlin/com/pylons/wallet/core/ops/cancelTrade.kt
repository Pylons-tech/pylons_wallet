package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core

fun Core.cancelTrade(tradeId : String) = engine.cancelTrade(tradeId).submit()