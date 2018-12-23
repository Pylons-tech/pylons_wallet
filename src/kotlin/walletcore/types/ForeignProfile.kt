package walletcore.types

/**
 * Internal state representation of a userProfile other than the user's own.
 */
data class ForeignProfile (
    val id : String = "",
    val strings : Map<String, String> = mapOf(),
    val knownCoins : Set<Coin> = setOf(),
    val knownItems : Set<Item> = setOf()
)