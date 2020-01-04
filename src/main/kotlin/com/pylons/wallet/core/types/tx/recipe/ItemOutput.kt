package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class ItemOutput(
        @property:[Json(name = "Doubles")]
        val doubles : List<DoubleParam>,
        @property:[Json(name = "Longs")]
        val longs : List<LongParam>,
        @property:[Json(name = "Strings")]
        val strings : List<StringParam>,
        @property:[NeverQuoteWrap Json(name = "Weight")]
        val weight : Int
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : ItemOutput =
                        ItemOutput (
                                doubles = DoubleParam.listFromJson(jsonObject.array<JsonObject>("Doubles")!!),
                                longs = LongParam.listFromJson(jsonObject.array<JsonObject>("Long")!!),
                                strings = StringParam.listFromJson(jsonObject.array<JsonObject>("Strings")!!),
                                weight = jsonObject.int("Weight")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>) : List<ItemOutput> {
                        val ls = mutableListOf<ItemOutput>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}