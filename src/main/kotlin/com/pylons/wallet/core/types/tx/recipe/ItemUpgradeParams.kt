package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject

data class ItemUpgradeParams(
        @property:[Json(name = "Doubles")]
        val doubles : List<DoubleInputParam>,
        @property:[Json(name = "Longs")]
        val longs : List<LongInputParam>,
        @property:[Json(name = "Strings")]
        val strings : List<StringInputParam>
) {
        companion object {

                fun fromJson (jsonObject: JsonObject) : ItemUpgradeParams = ItemUpgradeParams(
                        doubles = DoubleInputParam.listFromJson(jsonObject.array<JsonObject>("Doubles")),
                        longs = LongInputParam.listFromJson(jsonObject.array<JsonObject>("Long")),
                        strings = StringInputParam.listFromJson(jsonObject.array<JsonObject>("Strings"))
                )
        }
}