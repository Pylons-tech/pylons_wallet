package com.pylons.lib.types.tx

import com.beust.klaxon.*

data class TxData(
        @property:[Json(name = "msg")]
        val msg: String,
        @property:[Json(name = "status")]
        val status: String,
        @property:[Json(name = "output")]
        val output: List<TxDataOutput>
) {
    companion object {
        fun fromJson(jsonObject: JsonObject): TxData {
            val mList = mutableListOf<TxDataOutput>()
            /*
            jsonObject.array<JsonObject>("Output")?.forEach {
                mList.add(TxDataOutput.fromJson(it)
                        ?: throw Exception("Failed to parse message:\n ${it.toJsonString()}"))
            }
             */

            return TxData(
                    msg = jsonObject.string("Message") ?: "",
                    status = jsonObject.string("Status") ?: "",
                    output = mList
            )
        }
    }
}

data class TxDataOutput(
        @property:[Json(name = "type")]
        val type: String,
        @property:[Json(name = "coin")]
        val coin: String,
        @property:[Json(name = "amount")]
        val amount: Long,
        @property:[Json(name = "itemId")]
        val itemId: String
) {
    companion object {
        fun fromJson(jsonObject: JsonObject): TxDataOutput? {
            return TxDataOutput(
                    type = jsonObject.string("type") ?: "",
                    coin = jsonObject.string("coin") ?: "",
                    amount = jsonObject.long("amount") ?: 0,
                    itemId = jsonObject.string("itemID") ?: ""
            )
        }
    }
}