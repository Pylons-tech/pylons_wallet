package com.pylons.wallet.core.types.tx

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.types.klaxon

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