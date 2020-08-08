package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*

fun Core.sendPylons (q : Long, r : String) : Transaction = engine.sendPylons(q, r).submit()