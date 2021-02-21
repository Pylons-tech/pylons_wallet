package com.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class DoubleParam (
        @property:[Json(name = "Rate")]
        val rate : String,
        @property:[Json(name = "Key")]
        val key : String,
        @property:[Json(name = "WeightRanges")]
        val weightRanges : List<DoubleWeightRange>,
        @property:[Json(name = "Program")]
        val program : String) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : DoubleParam =
                        DoubleParam (
                                rate = jsonObject.string("Rate")!!,
                                key = jsonObject.string("Key")!!,
                                weightRanges = DoubleWeightRange.listFromJson(jsonObject.array("WeightRanges")!!),
                                program = jsonObject.string("Program")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<DoubleParam> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<DoubleParam>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}