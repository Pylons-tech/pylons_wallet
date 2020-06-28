package com.pylons.wallet.fixture_test.types

import com.beust.klaxon.Json

data class Item (
        @Json("stringKeys")
        val stringKeys : List<String>,
        @Json("stringValues")
        val stringValues : Map<String, String>,
        @Json("dblKeys")
        val doubleKeys : List<String>,
        @Json("dblValues")
        val doubleValues : Map<String, String>,
        @Json("longKeys")
        val longKeys : List<String>,
        @Json("longValues")
        val longValues : Map<String, Long>
)
