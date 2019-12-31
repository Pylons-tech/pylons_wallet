package com.pylons.wallet.core.types.tx.recipe

import com.squareup.moshi.Json

data class StringParam (
        @property:[Json(name = "Rate")]
        val rate : String,
        @property:[Json(name = "Key")]
        val key : String,
        @property:[Json(name = "Value")]
        val value : String)