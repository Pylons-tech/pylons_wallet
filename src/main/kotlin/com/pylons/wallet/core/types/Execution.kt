package com.pylons.wallet.core.types

import com.jayway.jsonpath.JsonPath
import com.squareup.moshi.Json
import net.minidev.json.JSONArray

data class Execution (
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
                fun getArrayFromJson(json : String) : Array<Execution> {
                        val doc = JsonPath.parse(json)
                        val jsonArray = doc.read<JSONArray>("$.Executions[*]")
                        val list = mutableListOf<Execution>()
                        for (i in jsonArray.indices) {
                                val root = "$.Executions[$i]"
                                val coinInputs = doc.read<JSONArray>("$root.CoinInputs")
                                val mCoinInputs = mutableListOf<Coin>()
                                if (coinInputs != null) {
                                        for (i2 in coinInputs.indices) {
                                                mCoinInputs.add(Coin(
                                                        doc.read<String>("$root.CoinInputs[$i2].Coin"),
                                                        doc.read<String>("$root.CoinInputs[$i2].Count").toLong()))
                                        }
                                }
                                val itemInputs = doc.read<JSONArray>("$root.ItemInputs")
                                val mItemInputs = mutableListOf<String>()
                                if (itemInputs != null) {
                                        for (i2 in itemInputs.indices) {
                                                mItemInputs.add(doc.read("$root.ItemInputs[$i2].ID"))
                                        }
                                }
                                list.add(
                                        Execution(
                                                id = doc.read<String>("$root.ID"),
                                                recipeId = doc.read<String>("$root.RecipeID"),
                                                cookbookId = doc.read<String>("$root.CookbookID"),
                                                completed = doc.read<Boolean>("$root.Completed"),
                                                sender = doc.read<String>("$root.Sender"),
                                                blockHeight = doc.read<String>("$root.BlockHeight").toLong(),
                                                coinInputs = mCoinInputs,
                                                itemInputs = mItemInputs
                                        )
                                )
                        }
                        return list.toTypedArray()
                }
        }
}