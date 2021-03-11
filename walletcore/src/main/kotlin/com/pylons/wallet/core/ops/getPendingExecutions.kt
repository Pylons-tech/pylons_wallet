package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.lib.types.types.*

fun Core.getPendingExecutions () : List<Execution> = engine.getPendingExecutions()