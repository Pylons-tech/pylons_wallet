package com.pylons.devdevwallet

import com.pylons.devdevwallet.controllers.CoreInteractEvent
import com.pylons.devdevwallet.controllers.WalletCoreController
import com.pylons.devdevwallet.views.StatusView
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.Multicore
import javafx.scene.layout.BorderPane
import tornadofx.*

@ExperimentalUnsignedTypes
class MainView : View() {
    private val walletCoreController: WalletCoreController by inject()
    private val statusView: StatusView by inject()
    override val root = BorderPane()

    override fun onDock() {
        walletCoreController
        fire(CoreInteractEvent {
            println("CoreInteract")
            Multicore.enable(Config)
        })
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
        title = getCoreStatusString(Core.current?.statusBlock?.walletCoreVersion.orEmpty(), Core.current?.started?.or(false)!!,
                Core.current?.sane?.or(false)!!, Core.current?.suspendedAction.orEmpty())
        statusView.master = this
        with (root) {
            prefWidth = 800.0
            prefHeight = 600.0
            bottom = statusView.root
        }
    }
}