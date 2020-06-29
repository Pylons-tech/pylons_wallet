package com.pylons.devwallet.views

import com.pylons.devwallet.controllers.ResultViewController
import com.pylons.wallet.core.types.tx.item.Item
import javafx.scene.layout.Priority
import tornadofx.*

class ListItemView : View() {
    private val resultViewController: ResultViewController by inject()

    override val root = tableview(resultViewController.items) {
        vgrow = Priority.ALWAYS
        readonlyColumn("ID", Item::id)
        readonlyColumn("Cookbook Id", Item::cookbookId)
        readonlyColumn("Sender", Item::sender)
        readonlyColumn("Owner Recipe ID", Item::ownerRecipeID)
        readonlyColumn("Owner Trade ID", Item::ownerTradeID)
        readonlyColumn("Tradable", Item::tradable)
        readonlyColumn("Last Update", Item::lastUpdate)
        readonlyColumn("Doubles", Item::doubles)
        readonlyColumn("Longs", Item::longs)
        readonlyColumn("Strings", Item::strings)
    }
}