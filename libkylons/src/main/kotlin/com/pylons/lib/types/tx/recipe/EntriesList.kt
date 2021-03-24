package com.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject

data class EntriesList(
        @property:[Json(name = "CoinOutputs")]
        val coinOutputs : List<CoinOutput>,
        @property:[Json(name = "ItemModifyOutputs")]
        val itemModifyOutputs : List<ItemModifyOutput>?,
        @property:[Json(name = "ItemOutputs")]
        val itemOutputs : List<ItemOutput>)  {

        companion object {
                fun fromJson (jsonObject : JsonObject?) : EntriesList? = when (jsonObject) {
                        null -> null
                        else -> EntriesList(
                                coinOutputs = CoinOutput.listFromJson(jsonObject.array("CoinOutputs")),
                                itemModifyOutputs = ItemModifyOutput.listFromJson(jsonObject.array("ItemModifyOutputs")),
                                itemOutputs = ItemOutput.listFromJson(jsonObject.array("ItemOutputs"))
                        )
                }
        }
}