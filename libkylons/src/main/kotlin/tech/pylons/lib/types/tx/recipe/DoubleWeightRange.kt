package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.NeverQuoteWrap

data class DoubleWeightRange(
        @property:[Json(name = "Upper")]
        val upper : String,
        @property:[Json(name = "Lower")]
        val lower : String,
        @property:[NeverQuoteWrap Json(name = "Weight")]
        val weight : Int
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : DoubleWeightRange =
                        DoubleWeightRange (
                                upper = jsonObject.string("Upper")!!,
                                lower = jsonObject.string("Lower")!!,
                                weight = jsonObject.int("Weight")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>) : List<DoubleWeightRange> {
                        val ls = mutableListOf<DoubleWeightRange>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}