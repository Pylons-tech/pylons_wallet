package com.pylons.devwallet

import com.pylons.devwallet.controllers.WalletCoreController
import com.pylons.devwallet.controllers.HeartbeatEvent
import com.pylons.devwallet.views.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Backend
import com.pylons.wallet.core.types.Config
import javafx.scene.layout.BorderPane
import tornadofx.*

@ExperimentalUnsignedTypes
class MainView : View() {
    private val walletCoreController: WalletCoreController by inject()
    private val statusView: StatusView by inject()
    private val menuView: MenuView by inject()
    private val actionView: ActionView by inject()
    private val resultView: ResultView by inject()
    override val root = BorderPane()

    override fun onDock() {
        walletCoreController
        //fire(BeginIPCPumpEvent)
        subscribe<HeartbeatEvent> { event ->
            getCoreStatusString(event.version, event.started, event.sane, event.suspendedAction)
        }
    }

    companion object {
        private fun getCoreStatusString(version: String, started: Boolean, sane: Boolean,
                                        suspendedAction: String): String =
                ("pylons devwallet core v$version " +
                        "started: $started, sane: $sane " +
                        "[suspended action: " +
                        "${if (suspendedAction == "") "none" else suspendedAction}]")

        private fun getChainStateString(height: String): String =
                "pylonschain height $height"
    }

    init {
        val keys = config.string("keys")
        if (keys != null) {
            Core.start(Config(
                    Backend.LIVE_DEV,
                    listOf("http://127.0.0.1:1317")
            ), keys)
        }

        title = getCoreStatusString(Core.VERSION_STRING, Core.started,
                Core.sane, Core.suspendedAction.orEmpty())
        with(root) {
            prefWidth = 800.0
            prefHeight = 600.0
            top = menuView.root
            bottom = statusView.root
            left = actionView.root
            center = resultView.root
        }
    }
}