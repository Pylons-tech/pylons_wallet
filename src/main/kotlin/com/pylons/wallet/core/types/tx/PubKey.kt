package com.pylons.wallet.core.types.tx

import com.squareup.moshi.Json

data class PubKey(
        @property:[Json(name = "type")]
        val type : String,
        @property:[Json(name = "value")]
        val value : String
)