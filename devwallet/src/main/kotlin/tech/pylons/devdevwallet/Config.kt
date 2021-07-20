package tech.pylons.devdevwallet

import tech.pylons.lib.types.Backend

val Config = tech.pylons.lib.types.Config(
        backend = Backend.MANUAL,
        chainId = "pylons-testnet",
        devMode = true,
        nodes = listOf("!27.0.0.1:1317")
)