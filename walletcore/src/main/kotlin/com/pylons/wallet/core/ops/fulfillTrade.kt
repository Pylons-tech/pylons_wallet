package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Transaction

fun Core.fulfillTrade(tradeId : String) : Transaction = engine.fulfillTrade(tradeId)