package com.pylons.wallet.fixture_test

import com.pylons.wallet.fixture_test.evtesting.TestContext
import com.pylons.wallet.fixture_test.types.FixtureStep
import java.util.function.Function

object ActionRunner {
    fun getActFunc (action : String) : Function<FixtureStep, TestContext> {
        when (action) {
            else -> throw NoSuchMethodException("Unrecognized action $action")
        }
    }
}