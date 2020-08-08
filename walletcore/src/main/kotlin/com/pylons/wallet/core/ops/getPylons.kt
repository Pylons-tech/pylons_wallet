package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*

fun Core.getPylons (q : Long) : Transaction = engine.getPylons(q).submit()