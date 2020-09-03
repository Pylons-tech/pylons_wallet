package com.pylons.wallet.core.types

import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.constants.ReservedKeys
import com.pylons.wallet.core.types.tx.item.Item

/**
 * Internal state representation of a userProfile other than the user's own.
 * TODO: refactor these to inherit from a shared base
 */
open class Profile (
        val address : String = "",
        val strings : Map<String, String>,
        var coins : List<Coin>,
        var items : List<Item>
) {
    fun getName () : String? = strings[ReservedKeys.profileName]

    fun coin (id : String) : Long {
        coins.forEach {
            if (it.denom == id) return it.amount
        }
        return 0
    }

    fun canPayCoins (coinsOut : List<Coin>) : Boolean {
        var ok = true
        coinsOut.forEach{
            if (coin(it.denom) < it.amount) ok = false
        }
        return ok
    }

    fun dump () : String = klaxon.toJsonString(this)
}