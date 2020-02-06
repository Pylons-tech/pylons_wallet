package com.pylons.wallet.core.types

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.pylons.wallet.core.types.tx.recipe.CoinInput
import com.pylons.wallet.core.types.tx.recipe.ItemInput
import com.pylons.wallet.core.types.tx.recipe.ItemUpgradeParams
import com.pylons.wallet.core.types.tx.recipe.WeightedParamList
import java.lang.StringBuilder

data class Recipe(
        @property:[Json(name = "ID")]
        val id : String,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "Disabled")]
        val disabled : Boolean,
        @property:[Json(name = "Name")]
        val name : String,
        @property:[Json(name = "Cookbook")]
        val cookbook : String,
        @property:[Json(name = "Description")]
        val description : String,
        @property:[Json(name = "BlockInterval")]
        val blockInterval : Long,
        @property:[Json(name = "CoinInputs")]
        val coinInputs : List<CoinInput>,
        @property:[Json(name = "ItemInputs")]
        val itemInputs : List<ItemInput>,
        @property:[Json(name = "Entries")]
        val entries : WeightedParamList,
        @property:[Json(name = "RType")]
        val recipeType : Long,
        @property:[Json(name = "ToUpgrade")]
        val upgradeParam : ItemUpgradeParams
) {
    companion object {
        fun getListFromJson(json : String) : List<Recipe> {
            val jsonArray = (Parser.default().parse(StringBuilder(json)) as JsonObject)
                    .array<JsonObject>("Recipes").orEmpty()
            val list = mutableListOf<Recipe>()
            jsonArray.forEach {
                list.add(
                        Recipe(
                                cookbook = it.string("CookbookID")!!,
                                name = it.string("Name")!!,
                                id = it.string("ID")!!,
                                description = it.string("Description")!!,
                                sender = it.string("Sender")!!,
                                blockInterval = it.string("BlockInterval")!!.toLong(),
                                disabled = it.boolean("Disabled")!!,
                                coinInputs = CoinInput.listFromJson(it.array("CoinInputs")),
                                itemInputs = ItemInput.listFromJson(it.array("ItemInputs")),
                                entries = WeightedParamList.fromJson(it.obj("Entries"))!!,
                                recipeType = it.string("RType")!!.toLong(),
                                upgradeParam = ItemUpgradeParams.fromJson(it.obj("ToUpgrade")!!)
                        )
                )
            }
            return list
        }
    }
}