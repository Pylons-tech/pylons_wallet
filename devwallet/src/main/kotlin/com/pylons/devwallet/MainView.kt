package com.pylons.devwallet

import com.pylons.devwallet.controllers.CoreStateController
import com.pylons.devwallet.controllers.CoreStateEvent
import com.pylons.devwallet.controllers.BeginRefreshingCoreStateEvent
import com.pylons.devwallet.views.StatusView
import com.pylons.wallet.core.Core
import javafx.scene.layout.BorderPane
import tornadofx.*

@ExperimentalUnsignedTypes
class MainView : View() {
    private val coreStateController: CoreStateController by inject()
    private val statusView: StatusView by inject()
    override val root = BorderPane()

    override fun onDock() {
        fire(BeginRefreshingCoreStateEvent)
        subscribe<CoreStateEvent> {
            title = getTitleStatus()
        }
        println("foobar")
    }

    companion object {
        private fun getTitleStatus () : String =
                ("pylons devwallet core v${Core.VERSION_STRING} " +
                "started: ${Core.started}, sane: ${Core.sane}")
    }

    init {
        title = getTitleStatus()
        statusView.master = this
        root.bottom = statusView.root

    }
}