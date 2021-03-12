package com.pylons.devdevwallet

import com.pylons.devdevwallet.controllers.CoreInteractEvent
import com.pylons.devdevwallet.controllers.WalletCoreController
import com.pylons.devdevwallet.controllers.HeartbeatEvent
import com.pylons.devdevwallet.controllers.IPCController
import com.pylons.devdevwallet.views.StatusView
import com.pylons.wallet.core.Core
import javafx.scene.layout.BorderPane
import tornadofx.*

@ExperimentalUnsignedTypes
class MainView : View() {
    private val ipcController: IPCController by inject()
    private val walletCoreController: WalletCoreController by inject()
    private val statusView: StatusView by inject()
    override val root = BorderPane()

    override fun onDock() {
        ipcController
        walletCoreController
        fire(CoreInteractEvent {
            println("CoreInteract")
            Core.start(Config, "")
        })
        subscribe<HeartbeatEvent> { event ->
            getCoreStatusString(event.version, event.started, event.sane, event.suspendedAction)
        }
        println("foobar")
    }

    companion object {
        private fun getCoreStatusString (version: String, started : Boolean, sane : Boolean,
        suspendedAction : String) : String =
                ("pylons devwallet org.bitcoinj.core.core v$version " +
                        "started: $started, sane: $sane " +
                        "[suspended action: " +
                        "${if (suspendedAction == "") "none" else suspendedAction}]")

        private fun getChainStateString (height: String) : String =
                "pylonschain height $height"
    }

    init {
        title = getCoreStatusString(Core.VERSION_STRING, Core.started,
                Core.sane, Core.suspendedAction.orEmpty())
        statusView.master = this
        with (root) {
            prefWidth = 800.0
            prefHeight = 600.0
            bottom = statusView.root
        }
    }
}