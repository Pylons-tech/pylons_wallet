package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.types.*

data class WeightedParamList(
        @property:[Json(name = "CoinOutputs")]
        val coinOutputs : List<CoinOutput>,
        @property:[Json(name = "ItemOutputs")]
        val itemOutputs : List<ItemOutput>)  {

        companion object {
                fun fromJson (jsonObject : JsonObject?) : WeightedParamList? = when (jsonObject) {
                        null -> null
                        else -> WeightedParamList(
                                coinOutputs = CoinOutput.listFromJson(jsonObject.array("CoinOutputs")),
                                itemOutputs = ItemOutput.listFromJson(jsonObject.array("ItemOutputs"))
                        )
                }
        }
}