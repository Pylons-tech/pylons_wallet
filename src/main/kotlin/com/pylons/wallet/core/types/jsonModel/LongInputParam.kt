package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class LongInputParam(
        @Json(name = "Key")
        val key : String,
        @Json(name = "MinValue")
        val minValue : Long,
        @Json(name = "MaxValue")
        val maxValue : Long
)

typealias LongInputParamList = List<LongInputParam>