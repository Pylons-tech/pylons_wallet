package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.lib.types.types.tx.msg.UpdateCookbook

val updateCookbookSignable = UpdateCookbook(
        id = "cookbook id",
        developer = "SketchyCo",
        description = "this has to meet character limits lol",
        supportEmail = "example@example.com",
        version = "1.0.0",
        sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"
)