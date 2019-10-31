package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class ItemOutput(
        @property:[Json(name = "Doubles")]
        val doubles : List<DoubleParam>,
        @property:[Json(name = "Longs")]
        val longs : List<LongParam>,
        @property:[Json(name = "Strings")]
        val strings : List<StringParam>,
        @property:[Json(name = "Weight")]
        val weight : Int
)