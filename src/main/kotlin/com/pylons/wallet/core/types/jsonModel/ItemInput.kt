package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class ItemInput(
        @Json(name = "Doubles")
        val doubles : DoubleInputParamList,
        @Json(name = "Longs")
        val longs : LongInputParamList,
        @Json(name = "Strings")
        val strings : StringInputParamList
)

typealias ItemInputList = List<ItemInput>