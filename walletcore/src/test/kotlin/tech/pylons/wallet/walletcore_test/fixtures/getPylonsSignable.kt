package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.msg.GetPylons

val getPylonsSignable = GetPylons(listOf(Coin("pylon", 500)),
        "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337")