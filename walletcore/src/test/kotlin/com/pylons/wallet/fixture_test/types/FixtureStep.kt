package com.pylons.wallet.fixture_test.types

import com.beust.klaxon.Json
import com.pylons.wallet.fixture_test.MsgRef
import com.pylons.wallet.fixture_test.Output
import com.pylons.wallet.fixture_test.RunAfter

data class FixtureStep (
        @Json("id")
        val id : String,
        @Json("runAfter")
        val runAfter : RunAfter,
        @Json("action")
        val action : String,
        @Json("paramsRef")
        val paramsRef : String,
        @Json("msgRefs")
        val msgRefs : List<MsgRef>,
        @Json("output")
        val output: Output
)