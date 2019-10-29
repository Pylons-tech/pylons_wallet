package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class LongParam (
        @Json(name = "Rate")
        val rate : String,
        @Json(name = "Key")
        val key : String,
        @Json(name = "WeightRanges")
        val weightRanges : LongWeightTable)

typealias LongParamList = List<LongParam>