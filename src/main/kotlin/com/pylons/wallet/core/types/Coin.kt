package com.pylons.wallet.core.types

import com.squareup.moshi.Moshi

/**
 * Local representation of a coin-type resource.
 */
data class Coin(
    val id: String = "",
    val count: Long = 0
)

fun Map<String, Long>.addCoins (other : Set<Coin>, subtractValues : Boolean) : Map<String, Long> {
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

fun Map<String, Long>.serializeCoins () : String {
    val sb = StringBuilder()
    forEach{
        sb.append("${it.key},${it.value},")
    }
    return when (sb.isNotEmpty()) {
        true -> sb.delete(sb.lastIndex, sb.length).toString()
        false -> ""
    }
}

fun List<Coin>.serialize () : String {
    val moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter<Coin>(Item::class.java)
    val sb = StringBuilder()
    forEach{
        sb.append("${jsonAdapter.toJson(it)},")
    }
    return when (sb.isNotEmpty()) {
        true -> sb.delete(sb.lastIndex, sb.length).toString()
        false -> ""
    }
}