package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject

data class EntriesList(
        @property:[Json(name = "coinOutputs")]
        val coinOutputs : List<CoinOutput>,
        @property:[Json(name = "itemOutputs")]
        val itemOutputs : List<ItemOutput>,
        @property:[Json(name = "itemModifyOutputs")]
        val itemModifyOutputs : List<ItemModifyOutput>)
          {

        companion object {
                fun fromJson (jsonObject : JsonObject?) : EntriesList? = when (jsonObject) {
                        null -> null
                        else -> EntriesList(
                                coinOutputs = CoinOutput.listFromJson(jsonObject.array("coinOutputs")),
                                itemOutputs = ItemOutput.listFromJson(jsonObject.array("itemOutputs")),
                                itemModifyOutputs = ItemModifyOutput.listFromJson(jsonObject.array("itemModifyOutputs"))

                        )
                }
        }
}