package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core

fun Core.checkExecution(id : String, payForCompletion : Boolean) = engine.checkExecution(id, payForCompletion)