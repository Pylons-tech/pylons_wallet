package com.pylons.devwallet.views

import com.pylons.devwallet.controllers.ResultViewController
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

class ResultView : View() {
    private val resultViewController: ResultViewController by inject()
    override val root = vbox {
        style {
            borderColor += box(Color.BLACK)
            padding = box(8.px)
        }
        textarea(resultViewController.results) {
            isEditable = false
            isWrapText = true
            vgrow = Priority.ALWAYS
        }
    }
}