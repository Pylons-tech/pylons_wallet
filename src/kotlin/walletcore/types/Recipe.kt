package walletcore.types

data class Recipe (
        val id : String = "",
        val coinsIn : Set<Coin> = setOf(),
        val itemsIn : Set<ItemPrototype> = setOf(),
        val itemsOut : Set<ItemPrototype> = setOf()
)