package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class LongInputParam(
        @property:[Json(name = "Key")]
        val key : String,
        @property:[Json(name = "MinValue")]
        val minValue : Long,
        @property:[Json(name = "MaxValue")]
        val maxValue : Long
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : LongInputParam =
                        LongInputParam (
                                key = jsonObject.string("Key")!!,
                                minValue = jsonObject.long("MinValue")!!,
                                maxValue = jsonObject.long("MaxValue")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>) : List<LongInputParam> {
                        val ls = mutableListOf<LongInputParam>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}