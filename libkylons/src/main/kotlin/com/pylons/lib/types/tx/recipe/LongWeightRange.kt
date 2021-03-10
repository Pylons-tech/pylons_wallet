package com.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.lib.types.tx.item.fuzzyInt
import com.pylons.lib.types.tx.item.fuzzyLong

data class LongWeightRange(
        @property:[NeverQuoteWrap Json(name = "Upper")]
        val upper : Long,
        @property:[NeverQuoteWrap Json(name = "Lower")]
        val lower : Long,
        @property:[NeverQuoteWrap Json(name = "Weight")]
        val weight : Int
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : LongWeightRange =
                        LongWeightRange (
                                upper = jsonObject.fuzzyLong("upper"),
                                lower = jsonObject.fuzzyLong("Lower"),
                                weight = jsonObject.fuzzyInt("Weight")
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>) : List<LongWeightRange> {
                        val ls = mutableListOf<LongWeightRange>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}