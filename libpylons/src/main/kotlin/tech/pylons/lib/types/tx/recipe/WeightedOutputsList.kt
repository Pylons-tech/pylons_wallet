package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.internal.fuzzyLong

data class WeightedOutput(
        @property:[Json(name = "entryIDs")]
        val entryIds : List<String>,
        @property:[Json(name = "weight")]
        val weight : Long
) {
    companion object {
        fun fromJson (jsonObject: JsonObject) : WeightedOutput =
                WeightedOutput (
                        entryIds = jsonObject.array<String>("entryIDs")!!.toList(),
                        weight = jsonObject.fuzzyLong("weight") ?: 0
                )

        fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<WeightedOutput> {
            if (jsonArray == null) return listOf()
            val ls = mutableListOf<WeightedOutput>()
            jsonArray.forEach { ls.add(fromJson(it)) }
            return ls
        }
    }
}