package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.internal.fuzzyLong
data class LongKeyValue (
    @property:[Json(name = "Key")]
        val Key : String,
    @property:[Json(name = "Value")]
        val Value : Long) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : LongKeyValue =
                    LongKeyValue (
                            Key = jsonObject.string("Key")!!,
                            Value = jsonObject.fuzzyLong("Value") ?: 0
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<LongKeyValue> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<LongKeyValue>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}