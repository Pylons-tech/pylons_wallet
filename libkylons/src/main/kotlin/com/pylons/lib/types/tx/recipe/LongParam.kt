package com.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class LongParam (
        @property:[Json(name = "Rate")]
        val rate : String,
        @property:[Json(name = "Key")]
        val key : String,
        @property:[Json(name = "WeightRanges")]
        val weightRanges : List<LongWeightRange>,
        @property:[Json(name = "Program")]
        val program : String) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : LongParam =
                        LongParam (
                                rate = jsonObject.string("Rate")!!,
                                key = jsonObject.string("Key")!!,
                                weightRanges = LongWeightRange.listFromJson(jsonObject.array("WeightRanges")!!),
                                program = jsonObject.string("Program")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<LongParam> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<LongParam>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}