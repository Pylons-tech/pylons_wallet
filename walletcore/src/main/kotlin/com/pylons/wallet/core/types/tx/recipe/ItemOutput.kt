package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class ItemOutput(
        @property:[Json(name = "Doubles")]
        val doubles : List<DoubleParam>,
        @property:[Json(name = "Longs")]
        val longs : List<LongParam>,
        @property:[Json(name = "ModifyItem")]
        val modifyItem : ItemUpgradeParams,
        @property:[Json(name = "Strings")]
        val strings : List<StringParam>,
        @property:[Json(name = "TransferFee") NeverQuoteWrap]
        val transferFee : Long
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : ItemOutput =
                        ItemOutput (
                                doubles = DoubleParam.listFromJson(jsonObject.array("Doubles")),
                                longs = LongParam.listFromJson(jsonObject.array("Long")),
                                strings = StringParam.listFromJson(jsonObject.array("Strings")),
                                // TODO: hard coded empty list?
                                modifyItem = ItemUpgradeParams(listOf(), listOf(), listOf(), 0),
                                transferFee = jsonObject.long("TransferFee")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<ItemOutput> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<ItemOutput>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}