package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class ItemHistory(
    @property:[Json(name = "ID")]
    val id: String,
    @property:[Json(name = "Owner")]
    val owner: String,
    @property:[Json(name = "ItemID")]
    val itemId: String,
    @property:[Json(name = "RecipeID")]
    val recipeId: String,
    @property:[Json(name = "TradeID")]
    val tradeId: String
) {
    companion object {
        fun fromJson (jsonObject: JsonObject) : ItemHistory =
            ItemHistory (
                id = jsonObject.string("ID")!!,
                owner = jsonObject.string("Owner")!!,
                itemId = jsonObject.string("ItemID")!!,
                recipeId = jsonObject.string("RecipeID")!!,
                tradeId = jsonObject.string("TradeID")!!
            )

        fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<ItemHistory> {
            if (jsonArray == null) return listOf()
            val ls = mutableListOf<ItemHistory>()
            jsonArray.forEach { ls.add(fromJson(it)) }
            return ls
        }
    }
}