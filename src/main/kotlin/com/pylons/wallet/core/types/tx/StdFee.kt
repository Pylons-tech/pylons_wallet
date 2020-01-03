package com.pylons.wallet.core.types.tx

import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.types.Coin
import com.squareup.moshi.Json

data class StdFee(
        @property:[Json(name = "amount")]
        val amount : Coin?,
        @property:[Json(name = "gas")]
        val gas : Long
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : StdFee {
                        return StdFee(
                                amount = when (val it = jsonObject.obj("amount")) {
                                        null -> null
                                        else -> Coin.fromJson(it)
                                },
                                gas = jsonObject.string("gas")!!.toLong()
                        )
                }
        }
}