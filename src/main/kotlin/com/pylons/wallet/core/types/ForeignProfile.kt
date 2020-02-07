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
    val knownCoins : List<Coin> = listOf()
) {
    fun detailsToMessageData () : MessageData {
        val msg = MessageData()
        val name = getName()
        if (name != null) msg.strings[Keys.NAME] = name
        msg.strings[Keys.ADDRESS] = id
        knownCoins.serializeCoinsToMessageData(msg)
        return msg
    }

    fun getName () : String? = strings[ReservedKeys.profileName]
}