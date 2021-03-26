package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.lib.types.tx.Coin
import com.pylons.lib.types.tx.msg.GetPylons

val getPylonsSignable = GetPylons(listOf(Coin("pylon", 500)),
        "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337")