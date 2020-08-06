package com.pylons.wallet.core.types

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.types.tx.item.Item

/**
 * Internal state representation of the user's own userProfile.
 */
@ExperimentalUnsignedTypes
data class Profile (
        var credentials: Credentials,
        val strings : MutableMap<String, String>,
        var coins : List<Coin>,
        var items : List<Item>,
        var lockedCoinDetails: LockedCoinDetails,
        /**
         * Mark profile as provisional if we haven't yet registered it (if needed) and retrieved a record of it
         * from the network.
         */
        var provisional : Boolean = false,
        val singletonGameRules : MutableList<String> = mutableListOf()
) {
    abstract class Credentials (var address : String) {
        abstract fun dumpToMessageData (msg : MessageData)
    }

    companion object {
        fun fromUserData () : Profile? {
            val data = UserData.dataSets.getValue(Core.engine.prefix)
            (Core.engine as TxPylonsEngine).cryptoHandler.importKeysFromUserData()
            val credentials = Core.engine.generateCredentialsFromKeys()
            return when (val name = data.getOrDefault("name", "")) {
                "" -> Profile(credentials = credentials, provisional = true,
                        coins = listOf(), items = listOf(), strings = mutableMapOf(), lockedCoinDetails = LockedCoinDetails("", listOf(), listOf(), listOf()))
                else -> Profile(credentials = credentials, strings = mutableMapOf(ReservedKeys.profileName to name),
                        provisional = true, coins = listOf(), items = listOf(), lockedCoinDetails = LockedCoinDetails("", listOf(), listOf(), listOf()))
            }
        }
    }

    fun getName () : String? = strings[ReservedKeys.profileName]

    fun detailsToMessageData () : MessageData {
        val msg = MessageData()
        val name = getName()
        if (name != null) msg.strings[Keys.NAME] = name
        else msg.strings[Keys.NAME] = ""
        msg.strings[Keys.ADDRESS]
        credentials.dumpToMessageData(msg)
        coins.serializeCoinsToMessageData(msg)
        msg.stringArrays[Keys.ITEMS] = mutableListOf()
        items.forEach {
            msg.stringArrays[Keys.ITEMS]!!.add(it.serialize())
        }
        return msg
    }

    fun countOfCoin (id : String) : Long {
        coins.forEach {
            if (it.denom == id) return it.amount
        }
        return 0
    }

    fun canPayCoins (coinsOut : List<Coin>) : Boolean {
        var ok = true
        coinsOut.forEach{
            if (countOfCoin(it.denom) < it.amount) ok = false
        }
        return ok
    }

    fun dump () : String = klaxon.toJsonString(this)
}