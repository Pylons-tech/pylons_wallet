package com.pylons.wallet.core.ops

import com.pylons.lib.types.Transaction
import com.pylons.wallet.core.Core

fun Core.getPylons (q : Long) : Transaction = engine.getPylons(q).submit()