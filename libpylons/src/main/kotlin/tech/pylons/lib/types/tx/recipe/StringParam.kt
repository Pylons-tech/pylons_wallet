package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class StringParam (
        @property:[Json(name = "key")]
        val key : String,
        @property:[Json(name = "rate")]
        val rate : String,
        @property:[Json(name = "value")]
        val value : String,
        @property:[Json(name = "program")]
        val program : String) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : StringParam =
                        StringParam (
                                key = jsonObject.string("key")!!,
                                rate = jsonObject.string("rate")!!,
                                value = jsonObject.string("value")!!,
                                program = jsonObject.string("program").orEmpty()
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<StringParam> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<StringParam>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}