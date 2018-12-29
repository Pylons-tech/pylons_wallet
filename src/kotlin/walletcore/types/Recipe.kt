package walletcore.types

data class Recipe (
        val id : String = "",
        val coinsIn : Set<Coin> = setOf(),
        val coinsOut : Set<Coin> = setOf(),
        val coinCatalysts : Set<Coin> = setOf(),
        val itemsIn : Set<ItemPrototype> = setOf(),
        val itemsOut : Set<ItemPrototype> = setOf(),
        val itemCatalysts : Set<ItemPrototype> = setOf()
)