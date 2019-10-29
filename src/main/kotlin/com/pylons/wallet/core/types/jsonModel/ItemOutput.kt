package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class ItemOutput(
        @Json(name = "Doubles")
        val doubles : DoubleParamList,
        @Json(name = "Longs")
        val longs : LongParamList,
        @Json(name = "Strings")
        val strings : StringParamList
)

typealias ItemOutputList = List<ItemOutput>