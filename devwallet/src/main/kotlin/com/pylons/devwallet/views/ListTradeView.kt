package com.pylons.devwallet.views

import com.pylons.devwallet.controllers.ResultViewController
import com.pylons.wallet.core.types.tx.Trade
import javafx.scene.layout.Priority
import tornadofx.*

class ListTradeView : View() {
    private val resultViewController: ResultViewController by inject()

    override val root = tableview(resultViewController.trades) {
        vgrow = Priority.ALWAYS
        readonlyColumn("ID", Trade::id)
        readonlyColumn("Coin Inputs", Trade::coinInputs)
        readonlyColumn("Item Inputs", Trade::itemInputs)
        readonlyColumn("Coin Outputs", Trade::coinOutputs)
        readonlyColumn("Item Outputs", Trade::itemOutputs)
        readonlyColumn("Extra Info", Trade::extraInfo)
        readonlyColumn("Sender", Trade::sender)
        readonlyColumn("Fulfiller", Trade::fulfiller)
        readonlyColumn("Disabled", Trade::disabled)
        readonlyColumn("Completed", Trade::completed)
    }
}