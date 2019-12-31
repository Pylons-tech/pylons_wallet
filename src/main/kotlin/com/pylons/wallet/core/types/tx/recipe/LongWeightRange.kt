package com.pylons.wallet.core.types.tx.recipe

import com.squareup.moshi.Json

data class LongWeightRange(
        @property:[NeverQuoteWrap Json(name = "Upper")]
        val upper : Long,
        @property:[NeverQuoteWrap Json(name = "Lower")]
        val lower : Long,
        @property:[NeverQuoteWrap Json(name = "Weight")]
        val weight : Int
)