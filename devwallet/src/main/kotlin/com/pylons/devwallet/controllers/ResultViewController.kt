package com.pylons.devwallet.controllers

import com.pylons.wallet.core.types.Cookbook
import com.pylons.wallet.core.types.tx.Trade
import com.pylons.wallet.core.types.tx.item.Item
import com.pylons.wallet.core.types.tx.recipe.Recipe
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class ResultViewController : Controller() {
    val results = SimpleStringProperty()
    val items = SimpleListProperty<Item>()
    val trades = SimpleListProperty<Trade>()
    val cookbooks = SimpleListProperty<Cookbook>()
    val recipes = SimpleListProperty<Recipe>()
}