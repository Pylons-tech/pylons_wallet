package com.pylons.wallet.core.types

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.fuzzyLong

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
                        amount = jsonObject.fuzzyLong("amount")
                )

        fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<Coin> {
            if (jsonArray == null) return listOf()
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