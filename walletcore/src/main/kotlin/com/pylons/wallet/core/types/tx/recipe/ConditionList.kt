package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class ConditionList(
    @property:[Json(name = "Doubles")]
    val doubles : List<DoubleInputParam>,
    @property:[Json(name = "Longs")]
    val longs : List<LongInputParam>,
    @property:[Json(name = "Strings")]
    val strings : List<StringInputParam>
) {
    companion object {
        fun fromJson (jsonObject: JsonObject?) : ConditionList? = when (jsonObject) {
            null -> null
            else -> ConditionList (
                doubles = DoubleInputParam.listFromJson(jsonObject.array("Doubles")),
                longs = LongInputParam.listFromJson(jsonObject.array("Longs")),
                strings = StringInputParam.listFromJson(jsonObject.array("Strings"))
            )
        }
    }
}