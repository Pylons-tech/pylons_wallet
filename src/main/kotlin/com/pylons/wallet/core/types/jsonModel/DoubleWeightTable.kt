package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class DoubleWeightTable(
        @Json(name = "Upper")
        val upper : String,
        @Json(name = "Lower")
        val lower : String,
        @Json(name = "weight")
        val weight : Int
)