package com.pylons.wallet.core.types

import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.item.Item
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

fun Map<String, Long>.serializeCoinsToMessageData (msg : MessageData) {
    val denoms = mutableListOf<String>()
    val counts = mutableListOf<Long>()
    forEach{
        denoms.add(it.key)
        counts.add(it.value)
    }
    msg.longArrays[Keys.COIN_COUNTS] = counts.toLongArray()
    msg.stringArrays[Keys.COIN_DENOMS] = denoms
}

fun List<Coin>.serializeCoinsToMessageData (msg : MessageData)  {
    val denoms = mutableListOf<String>()
    val counts = mutableListOf<Long>()
    forEach{
        denoms.add(it.id)
        counts.add(it.count)
    }
    msg.longArrays[Keys.COIN_COUNTS] = counts.toLongArray()
    msg.stringArrays[Keys.COIN_DENOMS] = denoms
}