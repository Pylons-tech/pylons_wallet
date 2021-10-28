package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class LongParam (
    @property:[Json(name = "key")]
        val key : String,
    @property:[Json(name = "weightRanges")]
        val weightRanges : List<IntWeightRange>,
    @property:[Json(name = "program")]
        val program : String) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : LongParam =
                        LongParam (
                            key = jsonObject.string("key").orEmpty(),
                                weightRanges = IntWeightRange.listFromJson(jsonObject.array("weightRanges")!!),
                                program = jsonObject.string("program").orEmpty()
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<LongParam> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<LongParam>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}