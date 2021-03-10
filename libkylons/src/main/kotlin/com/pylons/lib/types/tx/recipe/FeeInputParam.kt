package com.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.pylons.lib.types.tx.item.fuzzyLong

data class FeeInputParam(
        @property:[Json(name = "MinValue")]
        val minValue: Long,
        @property:[Json(name = "MaxValue")]
        val maxValue: Long
) {
    companion object {
        fun fromJson(jsonObject: JsonObject?): FeeInputParam =
                FeeInputParam(
                        minValue = jsonObject?.fuzzyLong("MinValue")!!,
                        maxValue = jsonObject?.fuzzyLong("MaxValue")!!
                )
    }
}