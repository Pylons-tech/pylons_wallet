package com.pylons.wallet.core.types

import com.squareup.moshi.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.*

/**
 * Internal state representation of the user's own userProfile.
 */
data class Profile (
        var credentials: Credentials,
        val strings : MutableMap<String, String>,
        var coins : MutableMap<String, Int>,
        val items : MutableList<Item>,
        /**
         * Mark profile as provisional if we haven't yet registered it (if needed) and retrieved a record of it
         * from the network.
         */
        var provisional : Boolean = false,
        val singletonGameRules : MutableList<String> = mutableListOf()
) {
    abstract class Credentials (val address : String) {
        abstract fun dumpToMessageData (msg : MessageData)
    }

    companion object {
        fun fromUserData () : Profile? {
            val data = UserData.dataSets.getValue(Core.engine!!.prefix)
            val credentials = Core.engine!!.getNewCredentials()
            return when (val name = data.getOrDefault("name", "")) {
                "" -> Profile(credentials = credentials, provisional = true,
                        coins = mutableMapOf(), strings = mutableMapOf(), items = mutableListOf())
                else -> Profile(credentials = credentials, strings = mutableMapOf(ReservedKeys.profileName to name),
                        provisional = true, coins = mutableMapOf(), items = mutableListOf())
            }
        }

        fun load (str : String) : Profile {
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter<Profile>(Profile::class.java)
            return adapter.fromJson(str)!!
        }
    }

    fun getName () : String? = strings[ReservedKeys.profileName]

    fun detailsToMessageData () : MessageData {
        val msg = MessageData()
        msg.booleans[Keys.profileExists] = true
        val name = getName()
        if (name != null) msg.strings[Keys.name] = name
        credentials.dumpToMessageData(msg)
        msg.strings[Keys.coins] = coins.serializeCoins()
        msg.stringArrays[Keys.items] = items.serialize()
        msg.stringArrays["singletonGameRules"] = singletonGameRules

        // and extras!
        return msg
    }

    fun countOfCoin (id : String) : Int {
        return when (coins[id]) {
            null -> 0
            else -> coins[id]!!
        }
    }

    fun canPayCoins (coinsOut : List<Coin>) : Boolean {
        var ok = true
        coinsOut.forEach{
            if (countOfCoin(it.id) < it.count) ok = false
        }
        return ok
    }

    fun canPayItems(itemProtos : List<ItemPrototype>) : Boolean {
        itemProtos.forEach {
            if (findItemForPrototype(it, emptySet()) == null) return false
        }
        return true
    }

    fun findItemForPrototype (prototype: ItemPrototype, prefs : Set<String>) : Item? {
        val foundItems = mutableSetOf<Item>()
        items.forEach {
            if (it.matchesPrototype(prototype)) foundItems.add(it)
        }
        if (foundItems.count() == 0) return null
        var selectedItem = foundItems.elementAt(0)
        prefs.forEach {
            foundItems.forEach {it2 ->
                if (it == it2.id) selectedItem = it2
            }
        }
        return selectedItem
    }

    fun dump () : String {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter<Profile>(Profile::class.java)
        return adapter.toJson(this)
    }
}