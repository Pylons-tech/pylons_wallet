package com.pylons.devwallet.views

import com.pylons.devwallet.controllers.ResultViewController
import com.pylons.wallet.core.types.Execution
import javafx.scene.layout.Priority
import tornadofx.*

class ListExecutionView : View() {
    private val resultViewController: ResultViewController by inject()

    override val root = tableview(resultViewController.executions) {
        vgrow = Priority.ALWAYS
        readonlyColumn("ID", Execution::id)
        readonlyColumn("Recipe Id", Execution::recipeId)
        readonlyColumn("Cookbook Id", Execution::cookbookId)
        readonlyColumn("Coin Inputs", Execution::coinInputs)
        readonlyColumn("Item Inputs", Execution::itemInputs)
        readonlyColumn("Block Height", Execution::blockHeight)
        readonlyColumn("Sender", Execution::sender)
        readonlyColumn("Completed", Execution::completed)
    }
}