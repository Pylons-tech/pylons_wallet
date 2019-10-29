package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class DoubleParam (
        @Json(name = "Rate")
        val rate : String,
        @Json(name = "Key")
        val key : String,
        @Json(name = "WeightRanges")
        val weightRanges : DoubleWeightTable)

typealias DoubleParamList = List<DoubleParam>