package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class ItemInput(
        @property:[Json(name = "Doubles")]
        val doubles : List<DoubleInputParam>,
        @property:[Json(name = "Longs")]
        val longs : List<LongInputParam>,
        @property:[Json(name = "Strings")]
        val strings : List<StringInputParam>
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : ItemInput =
                        ItemInput (
                                doubles = DoubleInputParam.listFromJson(jsonObject.array("Doubles")!!),
                                longs = LongInputParam.listFromJson(jsonObject.array("Long")!!),
                                strings = StringInputParam.listFromJson(jsonObject.array("Strings")!!)
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<ItemInput> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<ItemInput>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}