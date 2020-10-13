package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.constants.ReservedKeys
import com.pylons.wallet.core.internal.*
import com.pylons.wallet.core.types.*

fun Core.newProfile (name : String, kp : PylonsSECP256K1.KeyPair? = null) : Transaction {
    println("kp: $kp")
    return engine.registerNewProfile(name, kp).submit()
}