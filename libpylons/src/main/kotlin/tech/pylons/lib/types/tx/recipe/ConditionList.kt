package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject

data class ConditionList(
    @property:[Json(name = "doubles")]
    val doubles : List<DoubleInputParam>,
    @property:[Json(name = "longs")]
    val longs : List<LongInputParam>,
    @property:[Json(name = "strings")]
    val strings : List<StringInputParam>
) {
    companion object {
        fun fromJson (jsonObject: JsonObject?) : ConditionList? = when (jsonObject) {
            null -> null
            else -> ConditionList (
                doubles = DoubleInputParam.listFromJson(jsonObject.array("doubles")),
                longs = LongInputParam.listFromJson(jsonObject.array("longs")),
                strings = StringInputParam.listFromJson(jsonObject.array("strings"))
            )
        }
    }
}