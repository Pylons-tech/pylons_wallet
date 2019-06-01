package com.pylons.wallet.core.types

import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.constants.ReservedKeys

/**
 * Internal state representation of a userProfile other than the user's own.
 * TODO: refactor these to inherit from a shared base
 */
data class ForeignProfile (
    val id : String = "",
    val strings : Map<String, String> = mapOf(),
    val knownCoins : List<Coin> = listOf(),
    val knownItems : List<Item> = listOf()
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