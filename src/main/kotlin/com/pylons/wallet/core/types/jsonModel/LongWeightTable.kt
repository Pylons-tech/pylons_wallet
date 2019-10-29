package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class LongWeightTable(
        @Json(name = "Upper")
        val upper : Long,
        @Json(name = "Lower")
        val lower : Long,
        @Json(name = "weight")
        val weight : Int
)