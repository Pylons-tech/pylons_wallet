package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Json

data class StringParam (
        @Json(name = "Rate")
        val rate : String,
        @Json(name = "Key")
        val key : String,
        @Json(name = "Value")
        val value : String)

typealias StringParamList = List<StringParam>