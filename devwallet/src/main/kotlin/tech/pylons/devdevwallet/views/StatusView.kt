package tech.pylons.devdevwallet.views

import tornadofx.*

class StatusView : View() {
    var master : View? = null

    override val root = hbox {
        label()
    }
}