package com.pylons.wallet.core.types.tx.recipe

import com.pylons.wallet.core.types.*
import com.squareup.moshi.Json

data class ItemUpgradeParams(
        @property:[Json(name = "Doubles")]
        val doubles : List<DoubleInputParam>,
        @property:[Json(name = "Longs")]
        val longs : List<LongInputParam>,
        @property:[Json(name = "Strings")]
        val strings : List<StringInputParam>
) {
        companion object {
                val adapter = moshi.adapter<ItemUpgradeParams>(ItemUpgradeParams::class.java)

                fun fromJson (json : String) : ItemUpgradeParams? = adapter.fromJson(json)
        }
}