package com.pylons.wallet.core.types

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject

data class LockedCoin(
        @property:[Json(name = "NodeVersion")]
        val nodeVersion: String,
        @property:[Json(name = "Sender")]
        val sender: String,
        @property:[Json(name = "Amount")]
        val amount: List<Coin>
) {
    companion object {
        fun fromJson(jsonObject: JsonObject): LockedCoin =
                LockedCoin(
                        nodeVersion = jsonObject.string("NodeVersion")!!,
                        sender = jsonObject.string("Sender")!!,
                        amount = Coin.listFromJson(jsonObject.array("Amount"))
                )
    }
}