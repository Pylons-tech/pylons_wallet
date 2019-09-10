package com.pylons.wallet.core.types

import com.pylons.wallet.core.types.item.Item

data class BoundAssetSet (
        val coinsIn : Set<Coin> = setOf(),
        val coinsOut : Set<Coin> = setOf(),
        val itemsIn : Set<Item> = setOf(),
        val itemsOut : Set<Item> = setOf(),
        val coinsCatalysts : Set<Coin> = setOf(),
        val itemsCatalysts : Set<Item> = setOf()
)