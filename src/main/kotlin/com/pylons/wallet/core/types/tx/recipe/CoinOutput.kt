package com.pylons.wallet.core.types.tx.recipe

import com.pylons.wallet.core.types.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types

data class CoinOutput(
        @property:[Json(name = "Coin")]
        val coin : String,
        @property:[NeverQuoteWrap Json(name = "Count")]
        val count : Long,
        @property:[NeverQuoteWrap Json(name = "Weight")]
        val weight : Int
)  {
        companion object {
                val adapter : JsonAdapter<List<CoinOutput>> =
                        moshi.adapter(Types.newParameterizedType(List::class.java, CoinOutput::class.java))

                fun fromJson (json : String) : List<CoinOutput>? = adapter.fromJson(json)
        }
}