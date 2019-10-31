package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class LongWeightRange(
        @property:[Json(name = "Upper")]
        val upper : Long,
        @property:[Json(name = "Lower")]
        val lower : Long,
        @property:[Json(name = "Weight")]
        val weight : Int
)