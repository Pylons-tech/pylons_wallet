package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.*
import tech.pylons.lib.internal.fuzzyLong

data class CoinInput (
        @property:[Json(name = "Coin")]
        val coin : String,
        @property:[Json(name = "Count") QuotedJsonNumeral]
        val count : Long
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : CoinInput =
                        CoinInput (
                                coin = jsonObject.string("Coin")!!,
                                count = jsonObject.fuzzyLong("Count")
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<CoinInput> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<CoinInput>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}