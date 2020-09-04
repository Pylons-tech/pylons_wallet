package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.internal.fuzzyLong

data class CoinOutput(
        @property:[Json(name = "ID")]
        val id: String?,
        @property:[Json(name = "Coin")]
        val coin : String?,
        @property:[Json(name = "Count")]
        val count : String?
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : CoinOutput =
                        CoinOutput (
                                id = jsonObject.string("ID"),
                                coin = jsonObject.string("Coin"),
                                count = jsonObject.string("Count")
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<CoinOutput> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<CoinOutput>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}