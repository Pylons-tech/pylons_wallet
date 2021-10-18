package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.internal.fuzzyLong

data class LongInputParam(
        @property:[Json(name = "key")]
        val key: String,
        @property:[Json(name = "minValue")]
        val minValue: Long,
        @property:[Json(name = "maxValue")]
        val maxValue: Long
) {
    companion object {
        fun fromJson(jsonObject: JsonObject): LongInputParam =
                LongInputParam(
                        key = jsonObject.string("key")!!,
                        minValue = jsonObject.fuzzyLong("minValue"),
                        maxValue = jsonObject.fuzzyLong("maxValue")
                )

        fun listFromJson(jsonArray: JsonArray<JsonObject>?): List<LongInputParam> {
            if (jsonArray == null) return listOf()
            val ls = mutableListOf<LongInputParam>()
            jsonArray.forEach { ls.add(fromJson(it)) }
            return ls
        }
    }
}