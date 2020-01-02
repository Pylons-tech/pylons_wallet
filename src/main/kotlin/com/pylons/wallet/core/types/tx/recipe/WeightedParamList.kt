package com.pylons.wallet.core.types.tx.recipe

import com.pylons.wallet.core.types.*
import com.squareup.moshi.Json

data class WeightedParamList(
        @property:[Json(name = "CoinOutputs")]
        val coinOutputs : List<CoinOutput>,
        @property:[Json(name = "ItemOutputs")]
        val itemOutputs : List<ItemOutput>)  {

        companion object {
                val adapter = moshi.adapter<WeightedParamList>(WeightedParamList::class.java)

                fun fromJson (json : String) : WeightedParamList? = adapter.fromJson(json)
        }
}