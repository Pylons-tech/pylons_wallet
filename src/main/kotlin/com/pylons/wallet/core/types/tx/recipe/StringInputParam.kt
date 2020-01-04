package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class StringInputParam(
        @property:[Json(name = "Key")]
        val key : String,
        @property:[Json(name = "Value")]
        val value : String
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : StringInputParam =
                        StringInputParam (
                                key = jsonObject.string("Key")!!,
                                value = jsonObject.string("Value")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>) : List<StringInputParam> {
                        val ls = mutableListOf<StringInputParam>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}