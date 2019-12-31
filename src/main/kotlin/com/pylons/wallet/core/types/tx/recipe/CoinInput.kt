package com.pylons.wallet.core.types.tx.recipe

import com.squareup.moshi.Json

data class CoinInput (
        @property:[Json(name = "Coin")]
        val coin : String,
        @property:[Json(name = "Count") QuotedJsonNumeral]
        val count : Long
)