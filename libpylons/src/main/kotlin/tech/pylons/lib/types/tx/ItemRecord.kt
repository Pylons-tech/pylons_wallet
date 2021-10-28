package tech.pylons.lib.types.tx

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.QuotedJsonNumeral
import tech.pylons.lib.internal.fuzzyLong
import tech.pylons.lib.types.tx.recipe.DoubleKeyValue
import tech.pylons.lib.types.tx.recipe.LongKeyValue
import tech.pylons.lib.types.tx.recipe.StringKeyValue

/**
 * Local representation of a coin-type resource.
 */
data class ItemRecord(
    @property:[Json(name = "ID")]
        val ID: String,
    @property:[Json(name = "doubles")]
        val doubles: List<DoubleKeyValue>,
    @property:[Json(name = "longs")]
        val longs: List<LongKeyValue>,
    @property:[Json(name = "strings")]
        val strings: List<StringKeyValue>
) {
    companion object {
        fun fromJson (jsonObject: JsonObject) : ItemRecord =
            ItemRecord (
                ID = jsonObject.string("ID")!!,
                doubles = DoubleKeyValue.listFromJson(jsonObject.array("doubles")),
                longs = LongKeyValue.listFromJson(jsonObject.array("longs")),
                strings = StringKeyValue.listFromJson(jsonObject.array("strings"))
                )

        fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<ItemRecord> {
            if (jsonArray == null) return listOf()
            val ls = mutableListOf<ItemRecord>()
            jsonArray.forEach { ls.add(fromJson(it)) }
            return ls
        }
    }
}