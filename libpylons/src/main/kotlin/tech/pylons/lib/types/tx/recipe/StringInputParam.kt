package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class StringInputParam(
        @property:[Json(name = "key")]
        val key : String,
        @property:[Json(name = "value")]
        val value : String
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : StringInputParam =
                        StringInputParam (
                                key = jsonObject.string("key")!!,
                                value = jsonObject.string("value")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<StringInputParam> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<StringInputParam>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}