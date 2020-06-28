package com.pylons.wallet.fixture_test.types

import com.beust.klaxon.JsonArray

data class Scenario(
        val steps : List<FixtureStep>) {
    companion object {
        fun listFromJson(jsonArray: JsonArray<FixtureStep>?) : Scenario? = when(jsonArray) {
            null -> null
            else -> Scenario(jsonArray.toList())
        }
    }
}