package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.NeverQuoteWrap

data class DoubleWeightRange(
        @property:[Json(name = "lower")]
        val lower : String,
        @property:[Json(name = "upper")]
        val upper : String,
        @property:[NeverQuoteWrap Json(name = "weight")]
        val weight : Int
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : DoubleWeightRange =
                        DoubleWeightRange (
                                lower = jsonObject.string("lower")!!,
                                upper = jsonObject.string("upper")!!,
                                weight = jsonObject.int("weight")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>) : List<DoubleWeightRange> {
                        val ls = mutableListOf<DoubleWeightRange>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}