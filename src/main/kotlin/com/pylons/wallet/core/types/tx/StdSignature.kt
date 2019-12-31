package com.pylons.wallet.core.types.tx

import com.squareup.moshi.Json

data class StdSignature(
        @property:[Json(name = "signature")]
        val signature : String,
        @property:[Json(name = "pub_key")]
        val pubKey: PubKey
)