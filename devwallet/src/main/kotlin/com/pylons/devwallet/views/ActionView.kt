package com.pylons.devwallet.views

import com.pylons.devwallet.controllers.ActionViewController
import com.pylons.devwallet.fragments.GetTransactionFragment
import javafx.scene.paint.Color
import tornadofx.*

class ActionView : View() {
    private val actionViewController: ActionViewController by inject()
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
            }
        }
        button("Get Transaction") {
            useMaxWidth = true
            action {
                find<GetTransactionFragment>().openModal()
            }
        }
        button("List Items") {
            useMaxWidth = true
            action {
                actionViewController.getItems()
            }
        }
        button("List Trades") {
            useMaxWidth = true
            action {
                actionViewController.listTrades()
            }
        }
        button("List Cookbooks") {
            useMaxWidth = true
            action {
                actionViewController.listCookbooks()
            }
        }
        button("List Recipes") {
            useMaxWidth = true
            action {
                actionViewController.listRecipes()
            }
        }
    }
}