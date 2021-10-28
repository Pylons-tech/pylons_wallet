package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.msg.UpdateCookbook

val updateCookbookSignable = UpdateCookbook(
        Creator = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
        ID = "cookbook id",
        Name = "name",
        Description = "this has to meet character limits lol",
        Developer = "SketchyCo",
        Version = "1.0.0",
        SupportEmail = "example@example.com",
        CostPerBlock = Coin("pylons", 50),
        Enabled = true
)