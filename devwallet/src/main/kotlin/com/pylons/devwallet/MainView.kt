package com.pylons.devwallet

import com.pylons.devwallet.controllers.CoreInteractEvent
import com.pylons.devwallet.controllers.WalletCoreController
import com.pylons.devwallet.controllers.HeartbeatEvent
import com.pylons.devwallet.controllers.IPCController
import com.pylons.devwallet.views.StatusView
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Config
import javafx.scene.layout.BorderPane
import tornadofx.*
import java.io.ObjectInputFilter

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
                ("pylons devwallet core v$version " +
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