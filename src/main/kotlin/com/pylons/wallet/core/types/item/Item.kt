package com.pylons.wallet.core.types.item

import com.squareup.moshi.Moshi
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.ReservedKeys
import com.pylons.wallet.core.types.ConstraintMode
import com.pylons.wallet.core.types.ForeignProfile
import com.pylons.wallet.core.types.item.prototype.ItemPrototype

/**
 * Local representation of an item-type resource, implemented as a
 * set of maps containing K:V pairs of either String:String,
 * String:Long, or String:Double.
 */
data class Item(
        val id: String = "",
        val strings: Map<String, String> = mapOf(),
        val longs: Map<String, Long> = mapOf(),
        val doubles: Map<String, Double> = mapOf(),
        val cookbook : String,
        val sender : String
) {
    companion object {
        fun fromJson (json : String) : Item {
            val moshi = Moshi.Builder().build()
            val jsonAdapter = moshi.adapter<Item>(Item::class.java)
            return jsonAdapter.fromJson(json)!!
        }

        fun findInBufferedForeignProfile (profileId : String, itemId : String) : Item? {
            var prf : ForeignProfile? = null
            Core.foreignProfilesBuffer.forEach {
                if (it.id == profileId) prf = it
            }
            var item : Item? = null
            prf?.knownItems!!.forEach {
                if (it.id == itemId) item = it
            }
            return item
        }

        fun findInLocalProfile (itemId: String) : Item? {
            val prf = Core.userProfile
            var item : Item? = null
            prf?.items!!.forEach {
                if (it.id == itemId) item = it
            }
            return item
        }
    }

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

fun Set<Item>.exclude (other : Set<Item>) : Set<Item> {
    val mutable = this.toMutableSet()
    mutable.forEach {
        other.forEach {it2 ->
            if (it2.id == it.id) mutable.remove(it)
        }
    }
    return mutable.toSet()
}

fun Item.toJson () : String {
    val moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter<Item>(Item::class.java)
    return jsonAdapter.toJson(this)
}

fun List<Item>.serialize () : MutableList<String> {
    val moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter<Item>(Item::class.java)
    val ls = mutableListOf<String>()
    forEach{
        ls.add(jsonAdapter.toJson(it))
    }
    return ls
}