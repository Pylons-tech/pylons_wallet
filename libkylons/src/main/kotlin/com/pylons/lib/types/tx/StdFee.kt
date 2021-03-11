package com.pylons.lib.types.types.tx

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.internal.fuzzyLong
import com.pylons.lib.types.types.Coin

data class StdFee(
        @property:[Json(name = "amount")]
        val amount : List<Coin>,
        @property:[Json(name = "gas")]
        val gas : Long
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : StdFee {
                        return StdFee(
                                amount = Coin.listFromJson(jsonObject.array("amount")),
                                gas = jsonObject.fuzzyLong("gas")
                        )
                }
        }
}