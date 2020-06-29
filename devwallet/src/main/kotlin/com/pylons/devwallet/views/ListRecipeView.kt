package com.pylons.devwallet.views

import com.pylons.devwallet.controllers.ResultViewController
import com.pylons.wallet.core.types.tx.recipe.Recipe
import javafx.scene.layout.Priority
import tornadofx.*

class ListRecipeView : View() {
    private val resultViewController: ResultViewController by inject()

    override val root = tableview(resultViewController.recipes) {
        vgrow = Priority.ALWAYS
        readonlyColumn("ID", Recipe::id)
        readonlyColumn("Sender", Recipe::sender)
        readonlyColumn("Disabled", Recipe::disabled)
        readonlyColumn("Name", Recipe::name)
        readonlyColumn("Cookbook Id", Recipe::cookbookId)
        readonlyColumn("Description", Recipe::description)
        readonlyColumn("Block Interval", Recipe::blockInterval)
        readonlyColumn("Coin Inputs", Recipe::coinInputs)
        readonlyColumn("Item Inputs", Recipe::itemInputs)
        readonlyColumn("Entries", Recipe::entries)
        readonlyColumn("Outputs", Recipe::outputs)
    }
}