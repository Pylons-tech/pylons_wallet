package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.msg.CreateCookbook

val createCookbookSignable = CreateCookbook(
        creator = "pylo1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
        ID = "",
        name = "name",
        description = "this has to meet character limits lol",
        developer = "SketchyCo",
        version = "1.0.0",
        supportEmail = "example@example.com",
        costPerBlock = Coin("pylons", 50),
        enabled = true
)