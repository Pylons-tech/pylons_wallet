package walletcore.types

/**
 * Local representation of a coin-type resource.
 */
data class Coin(
    val id: String = "",
    val count: Int = 0
)

fun Map<String, Int>.addCoins (other : Set<Coin>, subtractValues : Boolean) : Map<String, Int> {
    val multi = when (subtractValues) {
        true -> -1
        false -> 1
    }
    val mutable = this.toMutableMap()
    other.forEach {
        if (mutable.containsKey(it.id)) mutable[it.id] = mutable[it.id]!! + it.count * multi
        else mutable[it.id] = it.count * multi
    }
    return mutable.toMap()
}

fun Map<String, Int>.serializeCoins () : String {
    val sb = StringBuilder()
    forEach{
        sb.append("${it.key},${it.value},")
    }
    return when (sb.isNotEmpty()) {
        true -> sb.delete(sb.lastIndex, sb.length).toString()
        false -> ""
    }
}