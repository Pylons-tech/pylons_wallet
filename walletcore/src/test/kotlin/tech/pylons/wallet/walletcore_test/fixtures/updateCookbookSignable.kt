package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.lib.types.tx.msg.UpdateCookbook

val updateCookbookSignable = UpdateCookbook(
        id = "cookbook id",
        developer = "SketchyCo",
        description = "this has to meet character limits lol",
        supportEmail = "example@example.com",
        version = "1.0.0",
        sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"
)