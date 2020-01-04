package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class LongParam (
        @property:[Json(name = "Rate")]
        val rate : String,
        @property:[Json(name = "Key")]
        val key : String,
        @property:[Json(name = "WeightRanges")]
        val weightRanges : List<LongWeightRange>) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : LongParam =
                        LongParam (
                                rate = jsonObject.string("Rate")!!,
                                key = jsonObject.string("Key")!!,
                                weightRanges = LongWeightRange.listFromJson(jsonObject.array("WeightRanges")!!)
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>) : List<LongParam> {
                        val ls = mutableListOf<LongParam>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}