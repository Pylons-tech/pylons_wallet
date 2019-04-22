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
        val longs: Map<String, Long> = mapOf(),
        val doubles: Map<String, Double> = mapOf()
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

    fun matchesPrototype (prototype: ItemPrototype) : Boolean {
        prototype.doubleConstraints.orEmpty().forEach { set ->
            set.value.forEach {
                if (!when (it.mode) {
                            ConstraintMode.KEY_EXISTS -> doubles.containsKey(set.key)
                            ConstraintMode.KEY_DOES_NOT_EXIST -> !doubles.containsKey(set.key)
                            ConstraintMode.EXACT_MATCH -> doubles[set.key] == it.value
                            ConstraintMode.NOT -> doubles[set.key] != null && doubles[set.key] != it.value
                            ConstraintMode.NUM_MORE_THAN -> doubles[set.key] != null && doubles[set.key]!! > it.value
                            ConstraintMode.NUM_LESS_THAN -> doubles[set.key] != null && doubles[set.key]!! < it.value
                            else -> false
                        }) return false
            }
        }
        prototype.longConstraints.orEmpty().forEach { set ->
            set.value.forEach {
                if (!when (it.mode) {
                            ConstraintMode.KEY_EXISTS -> longs.containsKey(set.key)
                            ConstraintMode.KEY_DOES_NOT_EXIST -> !longs.containsKey(set.key)
                            ConstraintMode.EXACT_MATCH -> longs[set.key] == it.value
                            ConstraintMode.NOT -> longs[set.key] != null && longs[set.key] != it.value
                            ConstraintMode.NUM_MORE_THAN -> longs[set.key] != null && longs[set.key]!! > it.value
                            ConstraintMode.NUM_LESS_THAN -> longs[set.key] != null && longs[set.key]!! < it.value
                            else -> false
                        }) return false
            }
        }
        prototype.stringConstraints.orEmpty().forEach { set ->
            set.value.forEach {
                if (!when (it.mode) {
                            ConstraintMode.KEY_EXISTS -> strings.containsKey(set.key)
                            ConstraintMode.KEY_DOES_NOT_EXIST -> !strings.containsKey(set.key)
                            ConstraintMode.EXACT_MATCH -> strings[set.key] == it.value
                            ConstraintMode.NOT -> strings[set.key] != null && strings[set.key] != it.value
                            ConstraintMode.STRING_INCLUDES -> strings[set.key] != null && strings[set.key]!!.contains(it.value)
                            ConstraintMode.STRING_EXCLUDES -> strings[set.key] != null && !strings[set.key]!!.contains(it.value)
                            else -> false
                        }) return false
            }
        }
        return true
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

fun Set<Item>.serialize () : MutableList<String> {
    val moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter<Item>(Item::class.java)
    val ls = mutableListOf<String>()
    forEach{
        ls.add(jsonAdapter.toJson(it))
    }
    return ls
}