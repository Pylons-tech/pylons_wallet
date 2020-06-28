package com.pylons.devwallet.controllers

import com.pylons.wallet.core.Core
import javafx.application.Platform
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import kotlin.concurrent.fixedRateTimer

class StatusViewController: Controller() {
    val height = SimpleLongProperty()
    val address = SimpleStringProperty()

    init {
        address.value = Core.userProfile?.credentials?.address
        fixedRateTimer("timer",false,0,5000){
            val status = Core.engine.getStatusBlock()
            Platform.runLater {
                height.value = status.height
            }
        }
    }
}