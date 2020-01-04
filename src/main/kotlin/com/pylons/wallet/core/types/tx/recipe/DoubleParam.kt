package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class DoubleParam (
        @property:[Json(name = "Rate")]
        val rate : String,
        @property:[Json(name = "Key")]
        val key : String,
        @property:[Json(name = "WeightRanges")]
        val weightRanges : List<DoubleWeightRange>) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : DoubleParam =
                        DoubleParam (
                                rate = jsonObject.string("Rate")!!,
                                key = jsonObject.string("Key")!!,
                                weightRanges = DoubleWeightRange.listFromJson(jsonObject.array("WeightRanges")!!)
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>) : List<DoubleParam> {
                        val ls = mutableListOf<DoubleParam>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}