package walletcore.types

import walletcore.Core
import walletcore.constants.*

/**
 * Internal state representation of the user's own userProfile.
 */
data class Profile (
    val id : String = "",
    val strings : Map<String, String> = mapOf(),
    val coins : Map<String, Int> = mapOf(),
    val items : Set<Item> = setOf(),
    /**
     * Mark profile as provisional if we haven't yet registered it (if needed) and retrieved a record of it
     * from the network.
     */
    val provisional : Boolean = false
) {
    companion object {
        fun fromUserData (userData: UserData?) : Profile? {
            return when (userData) {
                null -> null
                else -> {
                    val name = userData.name
                    val id = when (userData.id) {
                        null -> Core.txHandler.getNewUserId()
                        else -> userData.id
                    }
                    return when (name) {
                        null -> Profile(id = id, provisional = true)
                        else -> Profile(id = id, strings = mapOf(ReservedKeys.profileName to name), provisional = true)
                    }
                }
            }
        }
    }

    fun getName () : String? = strings[ReservedKeys.profileName]

    fun addCoins (c : Set<Coin>) : Profile = Profile(id, strings, coins.addCoins(c, false), items)

    fun addItems (i : Set<Item>) : Profile = Profile(id, strings, coins, items + i)

    fun removeCoins (c : Set<Coin>) : Profile = Profile(id, strings, coins.addCoins(c, true), items)

    fun removeItems (i : Set<Item>) : Profile = Profile(id, strings, coins, items.exclude(i))

    fun detailsToMessageData () : MessageData {
        val msg = MessageData()
        msg.booleans[Keys.profileExists] = true
        val name = getName()
        if (name != null) msg.strings[Keys.name] = name
        msg.strings[Keys.id] = id
        msg.strings[Keys.coins] = coins.serializeCoins()
        msg.stringArrays[Keys.items] = items.serialize()
        // and extras!
        return msg
    }

    fun countOfCoin (id : String) : Int {
        return when (coins[id]) {
            null -> 0
            else -> coins[id]!!
        }
    }

    fun canPayCoins (coinsOut : Set<Coin>) : Boolean {
        var ok = true
        coinsOut.forEach{
            if (countOfCoin(it.id) < it.count) ok = false
        }
        return ok
    }

    fun canPayItems(itemProtos : Set<ItemPrototype>) : Boolean {
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
}