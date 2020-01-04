package com.pylons.wallet.core.types

import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.types.tx.recipe.CoinInput
import com.pylons.wallet.core.types.tx.recipe.ItemInput
import com.pylons.wallet.core.types.tx.recipe.WeightedParamList

data class Recipe(val id : String, val sender : String, val disabled : Boolean, val name : String, val cookbook : String, val desc : String, val executionTime : Long,
                  val coinInputs : List<CoinInput>, val itemInputs : List<ItemInput>, val entries : WeightedParamList) {
    companion object {
        fun getArrayFromJson(json : String) : Array<Recipe> {
            val jsonArray = klaxon.parse<JsonObject>(json)!!.array<JsonObject>("Recipes")!!
            val list = mutableListOf<Recipe>()
            jsonArray.forEach {
                list.add(
                        Recipe(
                                cookbook = it.string("CookbookID")!!,
                                name = it.string("Name")!!,
                                id = it.string("ID")!!,
                                desc = it.string("Description")!!,
                                sender = it.string("Sender")!!,
                                executionTime = it.string("BlockInterval")!!.toLong(),
                                disabled = it.boolean("Disabled")!!,
                                coinInputs = CoinInput.listFromJson(it.array("CoinInputs")),
                                itemInputs = ItemInput.listFromJson(it.array("ItemInputs")),
                                entries = WeightedParamList.fromJson(it.obj("Entries"))!!
                        )
                )
            }
            return list.toTypedArray()
        }
    }
}

