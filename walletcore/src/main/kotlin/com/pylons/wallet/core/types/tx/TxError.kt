package com.pylons.wallet.core.types.tx

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject

data class TxError(
        @property:[Json(name = "code")]
        val code: Int,
        @property:[Json(name = "message")]
        val msg: String
) {
    companion object {
        fun fromJson(jsonObject: JsonObject): TxError {
            return TxError(
                    code = jsonObject.int("code") ?: 0,
                    msg = jsonObject.string("message") ?: ""
            )
        }
    }
}