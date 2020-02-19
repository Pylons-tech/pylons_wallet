package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

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
                                upper = jsonObject.long("Upper")!!,
                                lower = jsonObject.long("Lower")!!,
                                weight = jsonObject.int("Weight")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>) : List<LongWeightRange> {
                        val ls = mutableListOf<LongWeightRange>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}