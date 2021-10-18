package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.*

data class ItemInput(
    @property:[Json(name = "ID")]
    val id: String,
    @property:[Json(name = "doubles")]
    val doubles: List<DoubleInputParam>,
    @property:[Json(name = "longs")]
    val longs: List<LongInputParam>,
    @property:[Json(name = "strings")]
    val strings: List<StringInputParam>,
    @property:[Json(name = "conditions")]
    val conditions: ConditionList
) {
    companion object {
        fun fromJson(jsonObject: JsonObject): ItemInput =
            ItemInput(
                id = jsonObject.string("ID")!!,
                conditions = ConditionList.fromJson(jsonObject.obj("conditions")) ?: ConditionList(
                    listOf(),
                    listOf(),
                    listOf()
                ),
                doubles = DoubleInputParam.listFromJson(jsonObject.array("doubles")),
                longs = LongInputParam.listFromJson(jsonObject.array("longs")),
                strings = StringInputParam.listFromJson(jsonObject.array("strings"))
            )

        fun listFromJson(jsonArray: JsonArray<JsonObject>?): List<ItemInput> {
            if (jsonArray == null) return listOf()
            val ls = mutableListOf<ItemInput>()
            jsonArray.forEach { ls.add(fromJson(it)) }
            return ls
        }
    }
}