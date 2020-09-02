package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class ItemInput(
        @property:[Json(name = "ID")]
        val id: String,
        @property:[Json(name = "Doubles")]
        val doubles : List<DoubleInputParam>,
        @property:[Json(name = "Longs")]
        val longs : List<LongInputParam>,
        @property:[Json(name = "Strings")]
        val strings : List<StringInputParam>,
        @property:[Json(name = "TransferFee")]
        val transferFee: FeeInputParam
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : ItemInput =
                        ItemInput (
                                id = jsonObject.string("ID")!!,
                                doubles = DoubleInputParam.listFromJson(jsonObject.array("Doubles")),
                                longs = LongInputParam.listFromJson(jsonObject.array("Longs")),
                                strings = StringInputParam.listFromJson(jsonObject.array("Strings")),
                                transferFee = FeeInputParam.fromJson(jsonObject.obj("TransferFee"))
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<ItemInput> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<ItemInput>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}