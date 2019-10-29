package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class WeightedParamList(
        @Json(name = "CoinOutputs")
        val coinOutputs : CoinOutputList,
        @Json(name = "ItemOutputs")
        val itemOutputs : ItemOutputList)