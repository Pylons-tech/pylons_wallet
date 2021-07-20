package tech.pylons.lib.types

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import tech.pylons.lib.internal.fuzzyLong
import tech.pylons.lib.types.tx.Coin
import java.lang.StringBuilder

data class Execution (
        @property:[Json(name = "NodeVersion")]
        val nodeVersion : String,
        @property:[Json(name = "ID")]
        val id : String,
        @property:[Json(name = "RecipeID")]
        val recipeId : String,
        @property:[Json(name = "CookbookID")]
        val cookbookId : String,
        @property:[Json(name = "CoinInputs")]
        val coinInputs : List<Coin>,
        @property:[Json(name = "ItemInputs")]
        val itemInputs : List<String>,
        @property:[Json(name = "BlockHeight")]
        val blockHeight : Long,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "Completed")]
        val completed : Boolean
) {
        companion object {
                fun getListFromJson(json : String) : List<Execution> {
                        println(json)
                        val jsonArray =
                                (Parser.default().parse(StringBuilder(json)) as JsonObject).array<JsonObject>("Executions")
                        val list = mutableListOf<Execution>()
                        if (jsonArray != null && jsonArray.size > 0) for (i in jsonArray.indices) {
                                val entry = jsonArray[i]
                                list.add(
                                        Execution(
                                                nodeVersion = entry.string("NodeVersion")!!,
                                                id = entry.string("ID")!!,
                                                recipeId = entry.string("RecipeID")!!,
                                                cookbookId = entry.string("CookbookID")!!,
                                                completed = entry.boolean("Completed")!!,
                                                sender = entry.string("Sender")!!,
                                                blockHeight = entry.fuzzyLong("BlockHeight"),
                                                coinInputs = Coin.listFromJson(entry.array("CoinInputs")),
                                                itemInputs = entry.array("ItemInputs")?: listOf()
                                        )
                                )
                        }
                        return list
                }
        }
}