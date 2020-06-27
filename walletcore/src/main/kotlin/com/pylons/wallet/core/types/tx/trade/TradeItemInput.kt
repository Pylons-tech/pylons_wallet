package com.pylons.wallet.core.types.tx.trade

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.types.tx.recipe.DoubleInputParam
import com.pylons.wallet.core.types.tx.recipe.ItemInput
import com.pylons.wallet.core.types.tx.recipe.LongInputParam
import com.pylons.wallet.core.types.tx.recipe.StringInputParam

data class TradeItemInput(
        @property:[Json(name = "CookbookID" )]
        val cookbookId: String,
        @property:[Json(name = "ItemInput")]
        val itemInput: ItemInput
) {
    companion object {
        fun fromJson(jsonObject: JsonObject): TradeItemInput =
                TradeItemInput(
                        jsonObject.string("CookbookID") ?: "",
                        ItemInput(
                                doubles = DoubleInputParam.listFromJson(jsonObject.obj("ItemInput")?.array("Doubles")),
                                longs = LongInputParam.listFromJson(jsonObject.obj("ItemInput")?.array("Longs")),
                                strings = StringInputParam.listFromJson(jsonObject.obj("ItemInput")?.array("Strings"))
                        )
                )

        fun listFromJson(jsonArray: JsonArray<JsonObject>?): List<TradeItemInput> {
            if (jsonArray == null) return listOf()
            val ls = mutableListOf<TradeItemInput>()
            jsonArray.forEach { ls.add(fromJson(it)) }
            return ls
        }
    }
}