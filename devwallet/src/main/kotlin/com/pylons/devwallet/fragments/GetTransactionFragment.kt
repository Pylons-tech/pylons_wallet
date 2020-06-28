package com.pylons.devwallet.fragments

import com.pylons.devwallet.controllers.ActionViewController
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import tornadofx.*

class GetTransactionFragment: Fragment("Get Transaction") {
    private val actionViewController: ActionViewController by inject()
    private val input = SimpleStringProperty()

    override val root = form {
        prefWidth = 480.0
        fieldset(labelPosition = Orientation.VERTICAL) {
            field("Transaction Id") {
                textfield(input)
            }
        }
        button("Search") {
            action {
                actionViewController.getTransaction(input.value)
                this@GetTransactionFragment.close()
            }
        }
    }
}