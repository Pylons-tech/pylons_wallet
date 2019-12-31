package com.pylons.wallet.core.types.tx.recipe

import com.squareup.moshi.Json

data class DoubleInputParam(
        @property:[Json(name = "Key")]
        val key : String,
        @property:[Json(name = "MinValue")]
        val minValue : String,
        @property:[Json(name = "MaxValue")]
        val maxValue : String
)