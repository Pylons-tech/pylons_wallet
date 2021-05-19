package tech.pylons.devdevwallet

import tech.pylons.lib.types.Backend

val Config = tech.pylons.lib.types.Config(
        backend = Backend.LIVE_DEV,
        nodes = listOf("!27.0.0.1:1317")
)