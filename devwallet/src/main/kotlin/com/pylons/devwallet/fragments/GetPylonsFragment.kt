package com.pylons.devwallet.fragments

import com.pylons.devwallet.controllers.ActionViewController
import javafx.beans.property.SimpleLongProperty
import javafx.geometry.Orientation
import kotlinx.coroutines.runBlocking
import tornadofx.*

class GetPylonsFragment: Fragment("Get Pylons") {
    private val actionViewController: ActionViewController by inject()
    private val input = SimpleLongProperty()

    override val root = form {
        prefWidth = 250.0
        fieldset(labelPosition = Orientation.VERTICAL) {
            field("How many pylons?") {
                textfield(input)
            }
        }
        button("Submit") {
            action {
                runBlocking {
                    actionViewController.getPylons(input.value)
                    this@GetPylonsFragment.close()
                }
            }
        }
    }
}