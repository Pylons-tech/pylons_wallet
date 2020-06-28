package com.pylons.wallet.fixture_test.types

import com.beust.klaxon.Json

data class Output (
        @Json("txResult")
        val txResult: TxResult,
        @Json("property")
        val property: Property
)