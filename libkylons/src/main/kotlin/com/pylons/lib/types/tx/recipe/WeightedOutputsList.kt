package com.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class WeightedOutput(
        @property:[Json(name = "EntryIDs")]
        val entryIds : List<String>,
        @property:[Json(name = "Weight")]
        val weight : String
) {
    companion object {
        fun fromJson (jsonObject: JsonObject) : WeightedOutput =
                WeightedOutput (
                        entryIds = jsonObject.array<String>("EntryIDs")!!.toList(),
                        weight = jsonObject.string("Weight")!!
                )

        fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<WeightedOutput> {
            if (jsonArray == null) return listOf()
            val ls = mutableListOf<WeightedOutput>()
            jsonArray.forEach { ls.add(fromJson(it)) }
            return ls
        }
    }
}