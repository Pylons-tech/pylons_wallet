package com.pylons.wallet.core.internal

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*

internal fun bufferForeignProfile (id : String) : ForeignProfile? {
    val prf = Core.engine.getForeignBalances(id)
    return when (prf) {
        null -> null
        else -> {
            Core.foreignProfilesBuffer = Core.foreignProfilesBuffer + prf
            return prf
        }
    }
}