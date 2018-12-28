package walletcore.types

import com.squareup.moshi.Moshi
import walletcore.Core
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
    companion object {
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

fun Set<Item>.serialize () : String {
    val moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter<Item>(Item::class.java)
    val sb = StringBuilder()
    forEach{
        sb.append("${jsonAdapter.toJson(it)},")
    }
    return when (sb.isNotEmpty()) {
        true -> sb.delete(sb.lastIndex, sb.length).toString()
        false -> ""
    }
}