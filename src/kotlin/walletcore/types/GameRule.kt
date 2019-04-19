package walletcore.types

abstract class GameRule (
        open val id : String = "",
        open val coinsIn : Set<Coin>? = setOf(),
        open val coinsOut : Set<Coin>? = setOf(),
        open val coinCatalysts : Set<Coin>? = setOf(),
        open val itemsIn : Set<ItemPrototype>? = setOf(),
        open val itemsOut : Set<ItemPrototype>? = setOf(),
        open val itemCatalysts : Set<ItemPrototype>? = setOf()
) {
    @Transient
    var boundCoinsIn : Set<Coin> = setOf()
    @Transient
    var boundCoinsCatalysts : Set<Coin> = setOf()
    @Transient
    var boundItemsIn : Set<Item> = setOf()
    @Transient
    var boundItemsCatalysts : Set<Item> = setOf()

    open fun applyOffline () {
        throw Exception("This GameRule does not have a defined applyOffline override, so it can't be resolved by TxDummy.")
    }
    abstract fun bindInputsAndCatalysts () : Boolean
    abstract fun canApply () : Boolean
}