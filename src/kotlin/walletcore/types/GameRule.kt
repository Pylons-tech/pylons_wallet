package walletcore.types

abstract class GameRule (
        open val id : String = "",
        open val coinsIn : Set<Coin> = setOf(),
        open val coinsOut : Set<Coin> = setOf(),
        open val coinCatalysts : Set<Coin> = setOf(),
        open val itemsIn : Set<ItemPrototype> = setOf(),
        open val itemsOut : Set<ItemPrototype> = setOf(),
        open val itemCatalysts : Set<ItemPrototype> = setOf()
) {
    var boundCoinsIn : Set<Coin> = setOf()
    var boundCoinsCatalysts : Set<Coin> = setOf()
    var boundItemsIn : Set<Item> = setOf()
    var boundItemsCatalysts : Set<Item> = setOf()

    open fun applyOffline () {
        throw Exception("This GameRule does not have a defined applyOffline override, so it can't be resolved by TxDummy.")
    }
    abstract fun bindInputsAndCatalysts () : Boolean
    abstract fun canApply () : Boolean
}