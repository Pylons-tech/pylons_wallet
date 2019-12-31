package com.pylons.wallet.core.types.tx.recipe

import com.squareup.moshi.Json

data class LongInputParam(
        @property:[Json(name = "Key")]
        val key : String,
        @property:[Json(name = "MinValue")]
        val minValue : Long,
        @property:[Json(name = "MaxValue")]
        val maxValue : Long
)