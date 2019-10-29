package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class CoinInput (
        @Json(name = "Coin")
        val coin : String,
        @Json(name = "Count")
        val count : Long
)

typealias CoinInputList = List<CoinInput>