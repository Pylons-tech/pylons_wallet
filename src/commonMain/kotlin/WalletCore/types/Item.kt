package walletcore.types

import walletcore.constants.ReservedKeys

/**
 * Local representation of an item-type resource, implemented as a
 * set of maps containing K:V pairs of either String:String,
 * String:Long, or String:Double.
 */
data class Item(
    val id: String = "",
    val strings: Map<String, String> = mapOf(),
    val long: Map<String, Long> = mapOf(),
    val doubles: Map<String, Double> = mapOf()
) {
    /**
     * If the item has an entry in Item.strings for reserved key
     * itemName, return that. (This may be a literal name, or may
     * be a piece of data intended to be fed into a globalization
     * system in order to produce a human-readable name.)
     * Otherwise, return an anonymous-item designation.
     */
    fun name(): String {
        return when (strings.containsKey(ReservedKeys.itemName)) {
            true -> strings[ReservedKeys.itemName]!!
            false -> "Anonymous item|$id"
        }
    }
}