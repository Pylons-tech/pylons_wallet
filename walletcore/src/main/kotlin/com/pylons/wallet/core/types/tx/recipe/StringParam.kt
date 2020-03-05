package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class StringParam (
        @property:[Json(name = "Rate")]
        val rate : String,
        @property:[Json(name = "Key")]
        val key : String,
        @property:[Json(name = "Value")]
        val value : String,
        @property:[Json(name = "Program")]
        val program : String) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : StringParam =
                        StringParam (
                                rate = jsonObject.string("Rate")!!,
                                key = jsonObject.string("Key")!!,
                                value = jsonObject.string("Value")!!,
                                program = jsonObject.string("Program")!!
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<StringParam> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<StringParam>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}