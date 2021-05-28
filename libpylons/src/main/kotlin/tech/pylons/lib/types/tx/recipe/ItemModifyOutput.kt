package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.NeverQuoteWrap

data class ItemModifyOutput(
    @property:[Json(name = "ItemInputRef") NeverQuoteWrap]
        val itemInputRef: Int,
    @property:[Json(name = "Doubles")]
        val doubles: List<DoubleParam>,
    @property:[Json(name = "Longs")]
        val longs: List<LongParam>,
    @property:[Json(name = "Strings")]
        val strings: List<StringParam>,
    @property:[Json(name = "TransferFee") NeverQuoteWrap]
        val transferFee: Long
) {
    companion object {
        fun fromJson(jsonObject: JsonObject): ItemModifyOutput =
                ItemModifyOutput(
                        itemInputRef = jsonObject.int("ItemInputRef")!!,
                        doubles = DoubleParam.listFromJson(jsonObject.array("Doubles")),
                        longs = LongParam.listFromJson(jsonObject.array("Longs")),
                        strings = StringParam.listFromJson(jsonObject.array("Strings")),
                        transferFee = jsonObject.long("TransferFee")!!
                )

        fun listFromJson(jsonArray: JsonArray<JsonObject>?): List<ItemModifyOutput> {
            if (jsonArray == null) return listOf()
            val ls = mutableListOf<ItemModifyOutput>()
            jsonArray.forEach { ls.add(fromJson(it)) }
            return ls
        }
    }
}