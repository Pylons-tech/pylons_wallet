package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class StringInputParam(
        @Json(name = "Key")
        val key : String,
        @Json(name = "MinValue")
        val minValue : String,
        @Json(name = "MaxValue")
        val maxValue : String
)

typealias StringInputParamList = List<StringInputParam>