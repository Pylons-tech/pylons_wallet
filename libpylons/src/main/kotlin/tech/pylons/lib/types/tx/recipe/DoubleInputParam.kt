package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class DoubleInputParam(
        @property:[Json(name = "key")]
        val key : String,
        @property:[Json(name = "minValue")]
        val minValue : String,
        @property:[Json(name = "maxValue")]
        val maxValue : String
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : DoubleInputParam =
                        DoubleInputParam (
                                key = jsonObject.string("key")!!,
                                minValue = jsonObject.string("minValue")!!,
                                maxValue = jsonObject.string("maxValue")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<DoubleInputParam> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<DoubleInputParam>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}