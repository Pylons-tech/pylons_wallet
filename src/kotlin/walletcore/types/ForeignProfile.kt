package walletcore.types

import walletcore.constants.Keys
import walletcore.constants.ReservedKeys

/**
 * Internal state representation of a userProfile other than the user's own.
 * TODO: refactor these to inherit from a shared base
 */
data class ForeignProfile (
    val id : String = "",
    val strings : Map<String, String> = mapOf(),
    val knownCoins : Set<Coin> = setOf(),
    val knownItems : Set<Item> = setOf()
) {
    fun detailsToMessageData () : MessageData {
        val msg = MessageData()
        msg.booleans[Keys.profileExists] = true
        val name = getName()
        if (name != null) msg.strings[Keys.name] = name
        msg.strings[Keys.id] = id
        msg.strings[Keys.coins] = knownCoins.serialize()
        msg.stringArrays[Keys.items] = knownItems.serialize()
        // and extras!
        return msg
    }

    fun getName () : String? = strings[ReservedKeys.profileName]
}