package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.*

fun Core.getPendingExecutions () : List<Execution> = engine.getPendingExecutions()