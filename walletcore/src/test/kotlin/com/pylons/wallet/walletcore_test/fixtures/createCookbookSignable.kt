package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.wallet.core.types.tx.msg.CreateCookbook

val createCookbookSignable = CreateCookbook(
        cookbookId = "",
        name = "name",
        description = "this has to meet character limits lol",
        developer = "SketchyCo",
        version = "1.0.0",
        supportEmail = "example@example.com",
        sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
        level = 0,
        costPerBlock = 50
)