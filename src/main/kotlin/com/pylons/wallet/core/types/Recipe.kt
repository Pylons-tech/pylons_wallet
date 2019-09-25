package com.pylons.wallet.core.types

import com.jayway.jsonpath.JsonPath
import net.minidev.json.JSONArray

data class Recipe(val id : String, val sender : String, val disabled : Boolean, val name : String, val cookbook : String, val desc : String, val executionTime : Long,
                  val coinInputs : Map<String, Long>, val coinOutputs : Map<String, Long>) {
    companion object {
        fun getArrayFromJson(json : String) : Array<Recipe> {
            val doc = JsonPath.parse(json)
            val jsonArray = doc.read<JSONArray>("$.Recipes[*]")
            val list = mutableListOf<Recipe>()
            for (i in jsonArray.indices) {
                val root = "$.Recipes[$i]"
                val cookbookId = doc.read<String>("$root.CookbookId")
                val recipeName = doc.read<String>("$root.RecipeName")
                val id = doc.read<String>("$root.ID")
                val description = doc.read<String>("$root.Description")
                val sender = doc.read<String>("$root.Sender")
                val disabled = doc.read<Boolean>("$root.Disabled")
                val time = doc.read<String>("$root.BlockInterval").toLong()
                val coinInputs = doc.read<JSONArray>("$root.CoinInputs")
                val coinInputsMap = mutableMapOf<String, Long>()
                for (i2 in coinInputs.indices) {
                    coinInputsMap[doc.read<String>("$root.CoinInputs[$i2].Coin")] = doc.read<String>("$root.CoinInputs[$i2].Count").toLong()
                }
                val coinOutputs = doc.read<JSONArray>("$root.CoinOutputs")
                val coinOutputsMap = mutableMapOf<String, Long>()
                for (i2 in coinOutputs.indices) {
                    coinInputsMap[doc.read<String>("$root.CoinOutputs[$i2].Coin")] = doc.read<String>("$root.CoinOutputs[$i2].Count").toLong()
                }
                list.add(Recipe(id, sender, disabled, recipeName, cookbookId, description, time, coinInputsMap, coinOutputsMap))
            }
            return list.toTypedArray()
        }
    }
}

