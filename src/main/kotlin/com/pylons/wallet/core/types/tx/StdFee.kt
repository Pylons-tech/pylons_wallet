package com.pylons.wallet.core.types.tx

import com.pylons.wallet.core.types.Coin
import com.squareup.moshi.Json

data class StdFee(
        @property:[Json(name = "amount")]
        val amount : Coin?,
        @property:[Json(name = "gas")]
        val gas : Long
)