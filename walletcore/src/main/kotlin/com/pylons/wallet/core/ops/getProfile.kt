package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*

fun Core.getProfile (addr : String?) : Profile? {
    return when (addr) {
        null -> engine.getMyProfileState()
        else -> engine.getProfileState(addr)
    }
}