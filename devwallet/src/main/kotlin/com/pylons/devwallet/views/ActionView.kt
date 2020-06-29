package com.pylons.devwallet.views

import com.pylons.devwallet.controllers.ActionViewController
import com.pylons.devwallet.fragments.GetTransactionFragment
import javafx.scene.paint.Color
import tornadofx.*

class ActionView : View() {
    private val actionViewController: ActionViewController by inject()
    private var currentView: View = find<ResultView>()

    override val root = vbox {
        spacing = 12.0
        style {
            borderColor += box(Color.BLACK)
            padding = box(8.px)
        }
        button("Account") {
            useMaxWidth = true
            action {
                actionViewController.getAccount()
                currentView.replaceWith<ResultView>()
                currentView = find<ResultView>()
            }
        }
        button("Get Transaction") {
            useMaxWidth = true
            action {
                find<GetTransactionFragment>().openModal()
                currentView.replaceWith<ResultView>()
                currentView = find<ResultView>()
            }
        }
        button("List Items") {
            useMaxWidth = true
            action {
                actionViewController.getItems()
                currentView.replaceWith<ListItemView>()
                currentView = find<ListItemView>()
            }
        }
        button("List Trades") {
            useMaxWidth = true
            action {
                actionViewController.listTrades()
                currentView.replaceWith<ListTradeView>()
                currentView = find<ListTradeView>()
            }
        }
        button("List Cookbooks") {
            useMaxWidth = true
            action {
                actionViewController.listCookbooks()
                currentView.replaceWith<ListRecipeView>()
                currentView = find<ListRecipeView>()
            }
        }
        button("List Recipes") {
            useMaxWidth = true
            action {
                actionViewController.listRecipes()
                currentView.replaceWith<ListCookbookView>()
                currentView = find<ListCookbookView>()
            }
        }
    }
}