package com.pylons.devwallet.views

import tornadofx.*

class StatusView : View() {
    var master : View? = null

    override val root = hbox {
        label()
    }
}