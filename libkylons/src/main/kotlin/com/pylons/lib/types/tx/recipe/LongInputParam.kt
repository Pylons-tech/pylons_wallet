package com.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.lib.internal.fuzzyLong

data class LongInputParam(
        @property:[Json(name = "Key")]
        val key: String,
        @property:[Json(name = "MinValue")]
        val minValue: Long,
        @property:[Json(name = "MaxValue")]
        val maxValue: Long
) {
    companion object {
        fun fromJson(jsonObject: JsonObject): LongInputParam =
                LongInputParam(
                        key = jsonObject.string("Key")!!,
                        minValue = jsonObject.fuzzyLong("MinValue"),
                        maxValue = jsonObject.fuzzyLong("MaxValue")
                )

        fun listFromJson(jsonArray: JsonArray<JsonObject>?): List<LongInputParam> {
            if (jsonArray == null) return listOf()
            val ls = mutableListOf<LongInputParam>()
            jsonArray.forEach { ls.add(fromJson(it)) }
            return ls
        }
    }
}