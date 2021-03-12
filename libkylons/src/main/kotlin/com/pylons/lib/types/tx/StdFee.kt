package com.pylons.lib.types.tx

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.pylons.lib.internal.fuzzyLong

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