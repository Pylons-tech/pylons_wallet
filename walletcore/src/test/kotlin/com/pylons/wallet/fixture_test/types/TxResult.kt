package com.pylons.wallet.fixture_test.types

import com.beust.klaxon.Json

data class TxResult (
        @Json("status")
        val status : String,
        @Json("message")
        val message : String,
        @Json("errLog")
        val errorLog : String,
        @Json("broadcastError")
        val broadcastError : String
)