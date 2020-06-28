package com.pylons.wallet.fixture_test.types

import com.beust.klaxon.Json

data class Coin (
        @Json("denom")
        val denom : String,
        @Json("amount")
        val amount : Long
)
