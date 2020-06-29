package com.pylons.devwallet.views

import com.pylons.devwallet.controllers.ResultViewController
import com.pylons.wallet.core.types.Cookbook
import javafx.scene.layout.Priority
import tornadofx.*

class ListCookbookView : View() {
    private val resultViewController: ResultViewController by inject()

    override val root = tableview(resultViewController.cookbooks) {
        vgrow = Priority.ALWAYS
        readonlyColumn("ID", Cookbook::id)
        readonlyColumn("Name", Cookbook::name)
        readonlyColumn("Description", Cookbook::description)
        readonlyColumn("Version", Cookbook::version)
        readonlyColumn("Developer", Cookbook::developer)
        readonlyColumn("Level", Cookbook::level)
        readonlyColumn("Sender", Cookbook::sender)
        readonlyColumn("Support Email", Cookbook::supportEmail)
        readonlyColumn("Cost Per Block", Cookbook::costPerBlock)
    }
}