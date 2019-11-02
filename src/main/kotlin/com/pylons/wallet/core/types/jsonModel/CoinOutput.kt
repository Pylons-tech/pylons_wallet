package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class CoinOutput(
        @property:[Json(name = "Coin")]
        val coin : String,
        @property:[NeverQuoteWrap Json(name = "Count")]
        val count : Long,
        @property:[NeverQuoteWrap Json(name = "Weight")]
        val weight : Int
)