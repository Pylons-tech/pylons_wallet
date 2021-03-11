package com.pylons.lib.types.types.tx

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject

data class PubKey(
        @property:[Json(name = "type")]
        val type : String,
        @property:[Json(name = "value")]
        val value : String
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : PubKey = PubKey (
                        type = jsonObject.string("type")!!,
                        value = jsonObject.string("value")!!
                )
        }
}