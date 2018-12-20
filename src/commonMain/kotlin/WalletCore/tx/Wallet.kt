package walletcore.tx

import walletcore.types.Coin
import walletcore.types.Item

data class Wallet(
    val coins: MutableSet<Coin>,
    val items: MutableSet<Item>
)