package tech.pylons.lib.types

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import tech.pylons.lib.internal.fuzzyLong
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.ItemRecord
import java.lang.StringBuilder

data class Execution (
        @property:[Json(name = "creator")]
        val creator : String,
        @property:[Json(name = "ID")]
        val ID : String,
        @property:[Json(name = "recipeID")]
        val recipeId : String,
        @property:[Json(name = "cookbookID")]
        val cookbookID : String,
        @property:[Json(name = "recipeVersion")]
        val recipeVersion : String,
        @property:[Json(name = "nodeVersion")]
        val nodeVersion : String,
        @property:[Json(name = "blockHeight")]
        val blockHeight : Long,
        @property:[Json(name = "itemInputs")]
        val itemInputs : List<ItemRecord>,
        @property:[Json(name = "coinInputs")]
        val coinInputs : List<Coin>,
        @property:[Json(name = "coinOutputs")]
        val coinOutputs : List<Coin>,
        @property:[Json(name = "itemOutputIDs")]
        val itemOutputIDs : String,
        @property:[Json(name = "itemModifyOutputIDs")]
        val itemModifyOutputIDs : String
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
                                                creator = entry.string("creator")!!,
                                                ID = entry.string("ID")!!,
                                                recipeId = entry.string("recipeId")!!,
                                                cookbookID = entry.string("cookbookID")!!,
                                                recipeVersion = entry.string("recipeVersion")!!,
                                                nodeVersion = entry.string("nodeVersion")!!,
                                                blockHeight = entry.fuzzyLong("blockHeight") ?: 0,
                                                itemInputs = ItemRecord.listFromJson(entry.array("itemInputs")),
                                                coinInputs = Coin.listFromJson(entry.array("coinInputs")),
                                                coinOutputs = Coin.listFromJson(entry.array("coinOutputs")),
                                                itemOutputIDs = entry.string("itemOutputIDs")!!,
                                                itemModifyOutputIDs = entry.string("itemModifyOutputIDs")!!
                                        )
                                )
                        }
                        return list
                }

                fun parseFromJson(json : String) : Execution? {
                        try{
                                val obj = Parser.default().parse(StringBuilder(json)) as JsonObject
                                return Execution(
                                        creator = obj.string("creator")!!,
                                        ID = obj.string("ID")!!,
                                        recipeId = obj.string("recipeId")!!,
                                        cookbookID = obj.string("cookbookID")!!,
                                        recipeVersion = obj.string("recipeVersion")!!,
                                        nodeVersion = obj.string("nodeVersion")!!,
                                        blockHeight = obj.fuzzyLong("blockHeight") ?: 0,
                                        itemInputs = ItemRecord.listFromJson(obj.array("itemInputs")),
                                        coinInputs = Coin.listFromJson(obj.array("coinInputs")),
                                        coinOutputs = Coin.listFromJson(obj.array("coinOutputs")),
                                        itemOutputIDs = obj.string("itemOutputIDs")!!,
                                        itemModifyOutputIDs = obj.string("itemModifyOutputIDs")!!
                                )
                        }catch(e: Error){
                        }
                        return null
                }
        }
}