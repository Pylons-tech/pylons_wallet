package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class StringKeyValue (
    @property:[Json(name = "Key")]
        val Key : String,
    @property:[Json(name = "Value")]
        val Value : String) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : StringKeyValue =
                    StringKeyValue (
                            Key = jsonObject.string("Key")!!,
                            Value = jsonObject.string("Value")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<StringKeyValue> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<StringKeyValue>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}