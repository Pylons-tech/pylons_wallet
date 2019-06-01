package com.pylons.wallet.core.gamerules

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.OutsideWorldDummy
import com.pylons.wallet.core.types.UserData

internal class SimpleContractTest {

    private fun getJsonForRecipe (cookbook : String, recipe : String) : OutsideWorldDummy.GameRuleData {
        return OutsideWorldDummy.GameRuleData(
                type = "SimpleContract",
                json = com.pylons.wallet.core.fixtures.gamerule
        )
    }

    @Test
    fun parses () {
        OutsideWorldDummy.loadRuleJson = ::getJsonForRecipe
        val json = UserData("fooBar", "12345").exportAsJson()
        Core.uiInterrupts = InternalUiInterrupts()
        Core.start(json)
        val rule = OutsideWorldDummy.loadExternalGameRuleDef("a", "b")
        assertNotNull(rule)
        assertNotNull(rule.coinsOut)
        assertNotNull(rule.itemsOut)
        assertNotNull(rule.lootTables)
        assertNotNull(rule.lootTables!!.first().entries)
        assertNotNull(rule.lootTables!!.first().entries!!.first().items)
    }
}