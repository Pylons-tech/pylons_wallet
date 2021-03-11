package com.pylons.devdevwallet

import com.pylons.lib.types.types.Backend

val Config = com.pylons.lib.types.types.Config(
        backend = Backend.LIVE_DEV,
        nodes = listOf("!27.0.0.1:1317")
)