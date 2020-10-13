package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*

fun Core.getProfile (addr : String?) : Profile? {
    return when (addr) {
        // note: both null and "" are valid here. it depends on the serialization behavior
        // on the other side of the ipc link. so we have to check against both.
        null -> engine.getMyProfileState()
        "" -> engine.getMyProfileState()
        else -> engine.getProfileState(addr)
    }
}