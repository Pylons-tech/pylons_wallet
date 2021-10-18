package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.NeverQuoteWrap
import tech.pylons.lib.internal.fuzzyInt

data class IntWeightRange(
        @property:[NeverQuoteWrap Json(name = "lower")]
        val lower : Long,
        @property:[NeverQuoteWrap Json(name = "upper")]
        val upper : Long,
        @property:[NeverQuoteWrap Json(name = "weight")]
        val weight : Long
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : IntWeightRange =
                        IntWeightRange (
                                lower = jsonObject.long("lower")!!,
                                upper = jsonObject.long("upper")!!,
                                weight = jsonObject.long("weight")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>) : List<IntWeightRange> {
                        val ls = mutableListOf<IntWeightRange>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}