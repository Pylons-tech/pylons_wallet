package com.pylons.wallet.fixture_test.types

import com.beust.klaxon.Json

data class MsgRef (
        @Json("action")
        val action : String,
        @Json("paramsRef")
        val paramsRef : String
)