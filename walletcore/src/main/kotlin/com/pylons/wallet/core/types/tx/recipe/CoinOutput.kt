package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.internal.fuzzyLong

data class CoinOutput(
        @property:[Json(name = "denom")]
        val denom : String,
        @property:[Json(name = "amount") QuotedJsonNumeral]
        val amount : Long
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : CoinOutput =
                        CoinOutput (
                                denom = jsonObject.string("denom")!!,
                                amount = jsonObject.fuzzyLong("amount")
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<CoinOutput> {
                        println("listFromJson: $jsonArray")
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<CoinOutput>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}