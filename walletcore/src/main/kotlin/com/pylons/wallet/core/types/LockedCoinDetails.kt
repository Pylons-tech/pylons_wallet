package com.pylons.wallet.core.types

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject

data class LockedCoinDetails(
        @property:[Json(name = "Sender")]
        val sender: String,
        @property:[Json(name = "Amount")]
        val amount: List<Coin>,
        @property:[Json(name = "LockCoinTrades")]
        val lockCoinTrades: List<LockedCoinDescribe>,
        @property:[Json(name = "LockCoinExecs")]
        val lockCoinExecs: List<LockedCoinDescribe>
) {
    companion object {
        fun fromJson(jsonObject: JsonObject): LockedCoinDetails =
                LockedCoinDetails(
                        sender = jsonObject.string("Sender")!!,
                        amount = Coin.listFromJson(jsonObject.array("Amount")),
                        lockCoinTrades = LockedCoinDescribe.listFromJson(jsonObject.array("LockCoinTrades")),
                        lockCoinExecs = LockedCoinDescribe.listFromJson(jsonObject.array("LockCoinExecs"))
                )

        val default get() = LockedCoinDetails("", listOf(), listOf(), listOf())
    }
}