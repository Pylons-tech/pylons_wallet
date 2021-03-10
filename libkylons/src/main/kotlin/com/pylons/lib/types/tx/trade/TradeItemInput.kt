package com.pylons.wallet.core.types.tx.trade

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.lib.types.tx.recipe.*

data class TradeItemInput(
        @property:[Json(name = "CookbookID")]
        val cookbookId: String,
        @property:[Json(name = "ItemInput")]
        val itemInput: ItemInput
) {
    companion object {
        fun fromJson(jsonObject: JsonObject): TradeItemInput =
                TradeItemInput(
                        jsonObject.string("CookbookID") ?: "",
                        ItemInput(
                                id  = jsonObject.obj("ItemInput")!!.string("ID")!!,
                                conditions = ConditionList.fromJson(jsonObject.obj("Conditions"))?: ConditionList(listOf(), listOf(), listOf()),
                                doubles = DoubleInputParam.listFromJson(jsonObject.obj("ItemInput")?.array("Doubles")),
                                longs = LongInputParam.listFromJson(jsonObject.obj("ItemInput")?.array("Longs")),
                                strings = StringInputParam.listFromJson(jsonObject.obj("ItemInput")?.array("Strings")),
                                transferFee = FeeInputParam.fromJson(jsonObject.obj("ItemInput")?.obj("TransferFee"))
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