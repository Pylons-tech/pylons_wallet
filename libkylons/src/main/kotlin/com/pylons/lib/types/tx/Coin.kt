package com.pylons.lib.types.tx

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.lib.internal.fuzzyLong

/**
 * Local representation of a coin-type resource.
 */
data class Coin(
        @property:[Json(name = "denom")]
        val denom: String = "",
        @property:[Json(name = "amount")]
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

        fun Map<String, Long>.toCoinList () : List<Coin> {
            val l = this.entries.toList()
            return List(this.size) { Coin(l[it].key, l[it].value) }
        }
    }
}