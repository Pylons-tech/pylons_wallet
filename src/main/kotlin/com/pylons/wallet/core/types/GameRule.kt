package com.pylons.wallet.core.types

abstract class GameRule (
        open val id : String? = "",
        open val coinsIn : List<Coin>? = listOf(),
        open val coinsOut : List<Coin>? = listOf(),
        open val coinCatalysts : List<Coin>? = listOf(),
        open val itemsIn : List<ItemPrototype>? = listOf(),
        open val itemsOut : List<ItemPrototype>? = listOf(),
        open val itemCatalysts : List<ItemPrototype>? = listOf(),
        open val lootTables : List<LootTable>? = listOf()
) {
    @Transient
    var boundCoinsIn : List<Coin> = listOf()
    @Transient
    var boundCoinsCatalysts : List<Coin> = listOf()
    @Transient
    var boundItemsIn : List<Item> = listOf()
    @Transient
    var boundItemsCatalysts : List<Item> = listOf()

    open fun applyOffline () {
        throw Exception("This GameRule does not have a defined applyOffline override, so it can't be resolved by TxDummyEngine.")
    }
    abstract fun bindInputsAndCatalysts () : Boolean
    abstract fun canApply () : Boolean
}