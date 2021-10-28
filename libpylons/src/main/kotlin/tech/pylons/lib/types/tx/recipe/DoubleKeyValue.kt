package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.BigIntUtil

data class DoubleKeyValue (
    @property:[Json(name = "Key")]
        val Key : String,
    @property:[Json(name = "Value")]
        val Value : String) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : DoubleKeyValue =
                    DoubleKeyValue (
                            Key = jsonObject.string("Key")!!,
                            Value = BigIntUtil.long2bigInt(jsonObject.string("Value")!!),
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<DoubleKeyValue> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<DoubleKeyValue>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}