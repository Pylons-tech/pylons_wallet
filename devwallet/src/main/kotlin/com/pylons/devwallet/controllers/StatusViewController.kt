package com.pylons.devwallet.controllers

import com.pylons.wallet.core.Core
import javafx.application.Platform
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import kotlin.concurrent.fixedRateTimer

class StatusViewController : Controller() {
    val height = SimpleLongProperty()
    val address = SimpleStringProperty()
    val pylons = SimpleLongProperty()

    init {
        address.value = Core.userProfile?.credentials?.address
        updatePylons()

        fixedRateTimer("timer", false, 0, 5000) {
            val status = Core.engine.getStatusBlock()
            Platform.runLater {
                height.value = status.height
            }
        }
    }

    fun updatePylons() {
        val profile = Core.engine.getOwnBalances()
        pylons.value = profile?.countOfCoin("pylon")
    }
}