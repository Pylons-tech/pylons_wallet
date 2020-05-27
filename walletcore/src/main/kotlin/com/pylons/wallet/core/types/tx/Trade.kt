package com.pylons.wallet.core.types.tx

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.pylons.wallet.core.types.klaxon
import com.pylons.wallet.core.types.tx.item.Item
import com.pylons.wallet.core.types.tx.recipe.CoinInput
import com.pylons.wallet.core.types.tx.recipe.CoinOutput
import com.pylons.wallet.core.types.tx.recipe.ItemInput
import com.pylons.wallet.core.types.tx.recipe.ItemOutput
import java.lang.StringBuilder

data class Trade(
        @property:[Json(name = "ID")]
        val id : String,
        @property:[Json(name = "CoinInputs")]
        val coinInputs : List<CoinInput>,
        @property:[Json(name = "ItemInputs")]
        val itemInputs : List<ItemInput>,
        @property:[Json(name = "CoinOutputs")]
        val coinOutputs : List<CoinOutput>,
        @property:[Json(name = "ItemOutputs")]
        val itemOutputs: List<Item>,
        @property:[Json(name = "ExtraInfo")]
        val extraInfo : String,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "FulFiller")]
        val fulfiller : String,
        @property:[Json(name = "Disabled")]
        val disabled : Boolean,
        @property:[Json(name = "Completed")]
        val completed : Boolean
) {
    companion object {
        fun listFromJson (json : String) : List<Trade> {
            val jsonArray = (Parser.default().parse(StringBuilder(json)) as JsonObject)
                    .array<JsonObject>("Trades").orEmpty()
            val list = mutableListOf<Trade>()
            jsonArray.forEach {
                println(it.toJsonString())
                list.add(Trade(
                        id = it.string("ID")!!,
                        coinInputs = CoinInput.listFromJson(it.array("CoinInputs")),
                        itemInputs = ItemInput.listFromJson(it.array("ItemInputs")),
                        coinOutputs = CoinOutput.listFromJson(it.array("CoinOutputs")),
                        itemOutputs = Item.listFromJson(it.array("ItemOutputs")),
                        extraInfo = it.string("ExtraInfo").orEmpty(),
                        sender = it.string("Sender")!!,
                        fulfiller = it.string("Fulfiller").orEmpty(),
                        disabled = it.boolean("Disabled")!!,
                        completed = it.boolean("Completed")!!)
                )
            }
            return list
        }
    }
}