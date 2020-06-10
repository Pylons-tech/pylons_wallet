package com.pylons.wallet.core.types.tx

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.internal.fuzzyLong
import com.pylons.wallet.core.types.Coin

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
                                gas = jsonObject.fuzzyLong("gas")
                        )
                }
        }
}