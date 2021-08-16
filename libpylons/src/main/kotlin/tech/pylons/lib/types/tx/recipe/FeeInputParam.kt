package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import tech.pylons.lib.internal.fuzzyLong

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
                        maxValue = jsonObject.fuzzyLong("MaxValue")
                )
    }
}