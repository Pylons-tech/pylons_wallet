package com.pylons.devwallet.fragments

import com.pylons.devwallet.controllers.ActionViewController
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import kotlinx.coroutines.runBlocking
import tornadofx.*

class SendPylonsFragment : Fragment("Send Pylons") {
    private val actionViewController: ActionViewController by inject()
    private val amount = SimpleLongProperty()
    private val address = SimpleStringProperty()

    override val root = form {
        prefWidth = 250.0
        fieldset(labelPosition = Orientation.VERTICAL) {
            field("Address") {
                textfield(address)
            }
            field("How many pylons to send?") {
                textfield(amount)
            }
        }
        button("Submit") {
            action {
                runBlocking {
                    actionViewController.sendPylons(amount.value, address.value)
                    this@SendPylonsFragment.close()
                }
            }
        }
    }
}