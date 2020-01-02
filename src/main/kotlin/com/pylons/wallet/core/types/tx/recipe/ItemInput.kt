package com.pylons.wallet.core.types.tx.recipe

import com.pylons.wallet.core.types.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types

data class ItemInput(
        @property:[Json(name = "Doubles")]
        val doubles : List<DoubleInputParam>,
        @property:[Json(name = "Longs")]
        val longs : List<LongInputParam>,
        @property:[Json(name = "Strings")]
        val strings : List<StringInputParam>
) {
        companion object {
                val adapter : JsonAdapter<List<ItemInput>> =
                        moshi.adapter(Types.newParameterizedType(List::class.java, ItemInput::class.java))

                fun fromJson (json : String) : List<ItemInput>? = adapter.fromJson(json)
        }
}