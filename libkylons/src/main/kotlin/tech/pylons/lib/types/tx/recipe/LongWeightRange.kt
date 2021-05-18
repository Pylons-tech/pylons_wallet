package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.NeverQuoteWrap
import tech.pylons.lib.internal.fuzzyInt

data class LongWeightRange(
        @property:[NeverQuoteWrap Json(name = "Upper")]
        val upper : String,
        @property:[NeverQuoteWrap Json(name = "Lower")]
        val lower : String,
        @property:[NeverQuoteWrap Json(name = "Weight")]
        val weight : Int
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : LongWeightRange =
                        LongWeightRange (
                                upper = jsonObject.string("upper")!!,
                                lower = jsonObject.string("Lower")!!,
                                weight = jsonObject.fuzzyInt("Weight")
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>) : List<LongWeightRange> {
                        val ls = mutableListOf<LongWeightRange>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}