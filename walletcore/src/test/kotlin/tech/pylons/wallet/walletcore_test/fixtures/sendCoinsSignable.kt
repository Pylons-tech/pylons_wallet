package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.msg.SendCoins

val sendCoinsSignable = SendCoins(
        amount = listOf(
                Coin("loudcoin", 10),
                Coin("pylon", 10)
        ),
        receiver = "cosmos13rkt5rzf4gz8dvmwxxxn2kqy6p94hkpgluh8dj",
        sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"
)