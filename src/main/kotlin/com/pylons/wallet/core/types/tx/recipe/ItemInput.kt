package com.pylons.wallet.core.types.tx.recipe

import com.squareup.moshi.Json

data class ItemInput(
        @property:[Json(name = "Doubles")]
        val doubles : List<DoubleInputParam>,
        @property:[Json(name = "Longs")]
        val longs : List<LongInputParam>,
        @property:[Json(name = "Strings")]
        val strings : List<StringInputParam>
) {
        companion object {
                fun fromJson (json : String) : List<ItemInput> {

                }
        }
}