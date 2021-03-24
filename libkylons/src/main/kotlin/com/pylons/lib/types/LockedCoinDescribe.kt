package com.pylons.lib.types

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.lib.types.tx.Coin

data class LockedCoinDescribe(
        @property:[Json(name = "ID")]
        val id: String,
        @property:[Json(name = "Amount")]
        val amount: List<Coin>
) {
    companion object {
        fun fromJson(jsonObject: JsonObject): LockedCoinDescribe =
                LockedCoinDescribe(
                        id = jsonObject.string("ID")!!,
                        amount = Coin.listFromJson(jsonObject.array("Amount"))
                )

        fun listFromJson(jsonArray: JsonArray<JsonObject>?): List<LockedCoinDescribe> {
            if (jsonArray == null) return listOf()
            val ls = mutableListOf<LockedCoinDescribe>()
            jsonArray.forEach { ls.add(fromJson(it)) }
            return ls
        }
    }

}