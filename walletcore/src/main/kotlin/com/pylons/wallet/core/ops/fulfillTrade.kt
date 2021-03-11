package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.lib.types.types.Transaction

fun Core.fulfillTrade(tradeId : String, itemIds : List<String>) : Transaction =
        engine.fulfillTrade(tradeId, itemIds).submit()