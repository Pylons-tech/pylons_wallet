package com.pylons.devdevwallet

import com.pylons.wallet.core.types.Backend

val Config = com.pylons.wallet.core.types.Config(
        backend = Backend.LIVE_DEV,
        nodes = listOf("!27.0.0.1:1317")
)