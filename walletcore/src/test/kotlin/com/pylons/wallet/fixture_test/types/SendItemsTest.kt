package com.pylons.wallet.fixture_test.types

import com.beust.klaxon.Json

data class SendItemsTest(
        @Json("ItemNames")
        val itemNames : List<String>,
        @Json("Sender")
        val sender : String,
        @Json("Receiver")
        val receiver : String
)