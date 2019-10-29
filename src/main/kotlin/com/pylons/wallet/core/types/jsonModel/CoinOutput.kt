package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class CoinOutput(
        @Json(name = "Coin")
        val coin : String,
        @Json(name = "Count")
        val count : Long,
        @Json(name = "Weight")
        val weight : Int
)

typealias CoinOutputList = List<CoinOutput>