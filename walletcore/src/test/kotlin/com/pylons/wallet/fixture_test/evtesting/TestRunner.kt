package com.pylons.wallet.fixture_test.evtesting

import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage
import org.junit.platform.launcher.*
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass

object TestRunner {
    private val launcher = LauncherFactory.create()

    fun run (testClass : Class<*>) {
        val request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage("com.pylons.wallet.fixture_test"),
                selectClass(testClass)).build()
        launcher.discover(request)
        val listener = SummaryGeneratingListener()
        launcher.registerTestExecutionListeners(listener)
        launcher.execute(request, listener)
    }

}