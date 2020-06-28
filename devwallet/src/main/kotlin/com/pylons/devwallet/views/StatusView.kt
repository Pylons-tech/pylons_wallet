package com.pylons.devwallet.views

import com.pylons.devwallet.controllers.StatusViewController
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class StatusView : View() {
    private val statusViewController: StatusViewController by inject()

    override val root = hbox {
        style {
            borderColor += box(Color.BLACK)
            padding = box(8.px)
        }
        spacing = 12.0
        hbox {
            label("Address: ") {
                style {
                    fontWeight = FontWeight.BOLD
                }
            }
            label(statusViewController.address)
        }

        hbox {
            label("Height: ") {
                style {
                    fontWeight = FontWeight.BOLD
                }
            }
            label(statusViewController.height)
        }
    }
}