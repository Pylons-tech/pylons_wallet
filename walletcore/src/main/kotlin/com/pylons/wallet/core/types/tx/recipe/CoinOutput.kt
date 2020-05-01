package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class CoinOutput(
        @property:[Json(name = "Coin")]
        val coin : String,
        @property:[Json(name = "Count") QuotedJsonNumeral]
        val count : Long
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : CoinOutput =
                        CoinOutput (
                                coin = jsonObject.string("Coin")!!,
                                count = jsonObject.string("Count")!!.toLong()
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<CoinOutput> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<CoinOutput>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}