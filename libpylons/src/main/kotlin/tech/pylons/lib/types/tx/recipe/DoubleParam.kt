package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class DoubleParam (
    @property:[Json(name = "key")]
        val key : String,
    @property:[Json(name = "weightRanges")]
        val weightRanges : List<DoubleWeightRange>,
    @property:[Json(name = "program")]
        val program : String) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : DoubleParam =
                        DoubleParam (
                            key = jsonObject.string("key")!!,
                                weightRanges = DoubleWeightRange.listFromJson(jsonObject.array("weightRanges")!!),
                                program = jsonObject.string("program").orEmpty()
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<DoubleParam> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<DoubleParam>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}