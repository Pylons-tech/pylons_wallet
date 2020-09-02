package com.pylons.wallet.core.types

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.fuzzyLong
import com.pylons.wallet.core.types.tx.recipe.QuotedJsonNumeral

/**
 * Local representation of a coin-type resource.
 */
data class Coin(
        @property:[Json(name = "denom")]
        val denom: String = "",
        @property:[Json(name = "amount") QuotedJsonNumeral]
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