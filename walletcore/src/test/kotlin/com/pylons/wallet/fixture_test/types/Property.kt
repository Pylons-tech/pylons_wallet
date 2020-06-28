package com.pylons.wallet.fixture_test.types

import com.beust.klaxon.Json

data class Property (
        @Json("owner")
        val owner : String,
        @Json("shouldNotExist")
        val shouldNotExist : Boolean,
        @Json("cookbooks")
        val cookbooks : List<String>,
        @Json("recipes")
        val recipes : List<String>,
        @Json("items")
        val items : List<Item>,
        @Json("coins")
        val coins : List<Coin>
)