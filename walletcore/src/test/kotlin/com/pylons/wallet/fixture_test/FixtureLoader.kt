package com.pylons.wallet.fixture_test

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.google.common.io.Resources.getResource
import com.pylons.wallet.core.types.Cookbook
import com.pylons.wallet.core.types.Execution
import com.pylons.wallet.core.types.klaxon
import com.pylons.wallet.core.types.tx.Trade
import com.pylons.wallet.core.types.tx.recipe.Recipe
import com.pylons.wallet.fixture_test.types.FixtureStep
import com.pylons.wallet.fixture_test.types.Scenario
import com.pylons.wallet.fixture_test.types.SendItemsTest
import com.pylons.wallet.core.types.tx.item.Item as Item

object FixtureLoader {
    fun getCookbook(path : String) : Cookbook? =
            when (val it =
                    klaxon.parse<JsonObject>(getResource("fixtures/$path").path)) {
                null -> null
                else -> Cookbook.fromJson(it)
            }

    fun getExecution(path : String) : Execution? =
            when (val it =
                    klaxon.parse<JsonObject>(getResource("fixtures/$path").path)) {
                null -> null
                else -> Execution.fromJson(it)
            }

    fun getItem(path : String) : Item? =
            when (val it =
                    klaxon.parse<JsonObject>(getResource("fixtures/$path").path)) {
                null -> null
                else -> Item.fromJson(it)
            }

    fun getRecipe(path : String) : Recipe? =
            when (val it =
                    klaxon.parse<JsonObject>(getResource("fixtures/$path").path)) {
                null -> null
                else -> Recipe.fromJson(it)
            }

    fun getScenario (path : String) : Scenario? =
            Scenario.listFromJson(klaxon.parse(getResource("fixtures/$path").path))

    fun getSendItemsTest (path : String) : SendItemsTest? =
            klaxon.parse<SendItemsTest>(getResource("fixtures/$path").path)

    fun getTrade(path : String) : Trade? =
            when (val it =
                    klaxon.parse<JsonObject>(getResource("fixtures/$path").path)) {
                null -> null
                else -> Trade.fromJson(it)
            }
}