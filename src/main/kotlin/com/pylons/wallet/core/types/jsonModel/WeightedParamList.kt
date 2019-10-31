package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class WeightedParamList(
        @property:[Json(name = "CoinOutputs")]
        val coinOutputs : List<CoinOutput>,
        @property:[Json(name = "ItemOutputs")]
        val itemOutputs : List<ItemOutput>)