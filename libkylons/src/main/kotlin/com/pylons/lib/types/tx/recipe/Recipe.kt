package com.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.pylons.lib.internal.fuzzyLong
import java.lang.StringBuilder

data class Recipe(
    @property:[Json(name = "NodeVersion")]
        val nodeVersion : String,
    @property:[Json(name = "ID")]
        val id : String,
    @property:[Json(name = "Sender")]
        val sender : String,
    @property:[Json(name = "Disabled")]
        val disabled : Boolean,
    @property:[Json(name = "Name")]
        val name : String,
    @property:[Json(name = "CookbookID")]
        val cookbookId : String,
    @property:[Json(name = "Description")]
        val description : String,
    @property:[Json(name = "BlockInterval")]
        val blockInterval : Long,
    @property:[Json(name = "CoinInputs")]
        val coinInputs : List<CoinInput>,
    @property:[Json(name = "ItemInputs")]
        val itemInputs : List<ItemInput>,
    @property:[Json(name = "Entries")]
        val entries : EntriesList,
    @property:[Json(name = "Outputs")]
        val outputs : List<WeightedOutput>
) {
    companion object {
        fun listFromJson(json : String) : List<Recipe> {
            val jsonArray = (Parser.default().parse(StringBuilder(json)) as JsonObject)!!
                    .array<JsonObject>("recipes").orEmpty()
            val list = mutableListOf<Recipe>()
            jsonArray.forEach {
                list.add(
                        Recipe(
                                nodeVersion = it.string("NodeVersion")!!,
                                cookbookId = it.string("CookbookID")!!,
                                name = it.string("Name")!!,
                                id = it.string("ID")!!,
                                description = it.string("Description")!!,
                                sender = it.string("Sender")!!,
                                blockInterval = it.fuzzyLong("BlockInterval"),
                                disabled = it.boolean("Disabled")!!,
                                coinInputs = CoinInput.listFromJson(it.array("CoinInputs")),
                                itemInputs = ItemInput.listFromJson(it.array("ItemInputs")),
                                entries = EntriesList.fromJson(it.obj("Entries"))!!,
                                outputs = WeightedOutput.listFromJson(it.array("Outputs"))!!
                        )
                )
            }
            return list
        }
    }
}