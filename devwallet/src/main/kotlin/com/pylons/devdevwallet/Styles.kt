package com.pylons.devdevwallet

import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    init {
        Companion.label {
            fontSize = 11.px
            fontWeight = FontWeight.THIN
            backgroundColor += c("#ffffff")
        }
    }
}