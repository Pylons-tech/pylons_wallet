package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class DoubleParam (
        @property:[Json(name = "Rate")]
        val rate : String,
        @property:[Json(name = "Key")]
        val key : String,
        @property:[Json(name = "WeightRanges")]
        val weightRanges : List<DoubleWeightRange>)