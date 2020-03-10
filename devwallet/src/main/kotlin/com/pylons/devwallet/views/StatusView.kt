package com.pylons.devwallet.views

import tornadofx.*
import com.pylons.wallet.core.Core

class StatusView : View() {
    var master : View? = null

    override val root = hbox {
        label()
    }
}