package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.internal.fuzzyLong

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