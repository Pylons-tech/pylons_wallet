package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*

fun Core.getTransaction(txHash : String): Transaction = engine.getTransaction(txHash)