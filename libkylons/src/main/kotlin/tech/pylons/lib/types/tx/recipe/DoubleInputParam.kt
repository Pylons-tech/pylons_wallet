package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class DoubleInputParam(
        @property:[Json(name = "Key")]
        val key : String,
        @property:[Json(name = "MinValue")]
        val minValue : String,
        @property:[Json(name = "MaxValue")]
        val maxValue : String
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : DoubleInputParam =
                        DoubleInputParam (
                                key = jsonObject.string("Key")!!,
                                minValue = jsonObject.string("MinValue")!!,
                                maxValue = jsonObject.string("MaxValue")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<DoubleInputParam> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<DoubleInputParam>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}