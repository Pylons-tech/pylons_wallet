package com.pylons.wallet.core.types.tx.recipe

import com.pylons.wallet.core.types.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types

data class CoinInput (
        @property:[Json(name = "Coin")]
        val coin : String,
        @property:[Json(name = "Count") QuotedJsonNumeral]
        val count : Long
)  {
        companion object {
                val adapter : JsonAdapter<List<CoinInput>> =
                        moshi.adapter(Types.newParameterizedType(List::class.java, CoinInput::class.java))

                fun fromJson (json : String) : List<CoinInput>? = adapter.fromJson(json)
        }
}