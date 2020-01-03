package com.pylons.wallet.core.types

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.constants.Keys

/**
 * Local representation of a coin-type resource.
 */
data class Coin(
        @Json("denom")
        val denom: String = "",
        @Json("amount")
        val amount: Long = 0
) {
    companion object {
        fun fromJson (jsonObject: JsonObject) : Coin =
                Coin (
                        denom = jsonObject.string("denom")!!,
                        amount = jsonObject.string("amount")!!.toLong()
                )

        fun listFromJson (jsonArray: JsonArray<JsonObject>) : List<Coin> {
            val ls = mutableListOf<Coin>()
            jsonArray.forEach { ls.add(fromJson(it)) }
            return ls
        }
    }
}

fun Map<String, Long>.addCoins (other : Set<Coin>, subtractValues : Boolean) : Map<String, Long> {
    val multi = when (subtractValues) {
        true -> -1
        false -> 1
    }
    val mutable = this.toMutableMap()
    other.forEach {
        if (mutable.containsKey(it.denom)) mutable[it.denom] = mutable[it.denom]!! + it.amount * multi
        else mutable[it.denom] = it.amount * multi
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
        denoms.add(it.denom)
        counts.add(it.amount)
    }
    msg.longArrays[Keys.COIN_COUNTS] = counts.toLongArray()
    msg.stringArrays[Keys.COIN_DENOMS] = denoms
}