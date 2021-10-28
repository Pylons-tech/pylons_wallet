package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.types.tx.Coin

data class CoinOutput(
        @property:[Json(name = "ID")]
        val id: String?,
        @property:[Json(name = "coin")]
        val coin : Coin?,
        @property:[Json(name = "program")]
        val program : String?
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : CoinOutput =
                        CoinOutput (
                                id = jsonObject.string("ID"),
                                coin = Coin.fromJson(jsonObject.obj("coin")!!),
                                program = jsonObject.string("program")
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<CoinOutput> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<CoinOutput>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}