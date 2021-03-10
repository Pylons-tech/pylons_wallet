package com.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject

data class ItemUpgradeParams(
    @property:[Json(name = "Doubles")]
        val doubles : List<DoubleInputParam>,
    @property:[Json(name = "Longs")]
        val longs : List<LongInputParam>,
    @property:[Json(name = "Strings")]
        val strings : List<StringInputParam>,
    @property:[Json(name = "TransferFee") NeverQuoteWrap]
        val transferFee : Long
) {
        companion object {

                fun fromJson (jsonObject: JsonObject) : ItemUpgradeParams = ItemUpgradeParams(
                        doubles = DoubleInputParam.listFromJson(jsonObject.array("Doubles")),
                        longs = LongInputParam.listFromJson(jsonObject.array("Long")),
                        strings = StringInputParam.listFromJson(jsonObject.array("Strings")),
                        transferFee = jsonObject.long("TransferFee")!!
                )
        }
}