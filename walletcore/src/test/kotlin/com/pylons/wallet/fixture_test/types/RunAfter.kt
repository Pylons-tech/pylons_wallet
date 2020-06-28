package com.pylons.wallet.fixture_test.types

import com.beust.klaxon.Json

data class RunAfter (
        @Json("precondition")
        val preconditions : List<String>,
        @Json("blockWait")
        val blockWait : Long
)