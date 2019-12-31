package com.pylons.wallet.core.types.tx.recipe

import com.squareup.moshi.Json

data class DoubleWeightRange(
        @property:[Json(name = "Upper")]
        val upper : String,
        @property:[Json(name = "Lower")]
        val lower : String,
        @property:[NeverQuoteWrap Json(name = "Weight")]
        val weight : Int
)