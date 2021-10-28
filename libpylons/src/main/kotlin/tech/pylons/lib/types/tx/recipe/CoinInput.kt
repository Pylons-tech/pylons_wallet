package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.*
import tech.pylons.lib.internal.fuzzyLong
import tech.pylons.lib.types.tx.Coin

data class CoinInput (
        @property:[Json(name = "coins")]
        val coin : List<Coin>
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : CoinInput =
                        CoinInput (
                                coin = Coin.listFromJson(jsonObject.array("coins"))
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<CoinInput> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<CoinInput>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}