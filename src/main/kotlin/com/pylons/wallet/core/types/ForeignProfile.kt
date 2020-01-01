package com.pylons.wallet.core.types

import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.constants.ReservedKeys
import com.pylons.wallet.core.types.item.Item
import com.pylons.wallet.core.types.item.serialize

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
        val name = getName()
        if (name != null) msg.strings[Keys.NAME] = name
        msg.strings[Keys.ADDRESS] = id
        knownCoins.serializeCoinsToMessageData(msg)
        msg.stringArrays[Keys.ITEMS] = knownItems.serialize()
        return msg
    }

    fun getName () : String? = strings[ReservedKeys.profileName]
}