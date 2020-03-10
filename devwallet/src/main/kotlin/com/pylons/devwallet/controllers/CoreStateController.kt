package com.pylons.devwallet.controllers

import com.pylons.wallet.core.Core
import javafx.animation.Animation.INDEFINITE
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.util.Duration
import tornadofx.*
import tornadofx.EventBus.RunOn.BackgroundThread

object CoreStateRequest : FXEvent(BackgroundThread)

class CoreStateEvent(val started : Boolean, val sane : Boolean,
                     val suspendedAction : String) : FXEvent()

const val HEARTBEAT_INTERVAL = 1.0

object BeginRefreshingCoreStateEvent : FXEvent()

@ExperimentalUnsignedTypes
class CoreStateController : Controller() {
    private var timeline : Timeline? = null

    init {
        println("foo")
        subscribe<BeginRefreshingCoreStateEvent> { startTimer() }
        subscribe<CoreStateRequest> {
            val started = Core.started
            val sane = Core.sane
            val suspendedAction = Core.suspendedAction.orEmpty()
            fire(CoreStateEvent(started, sane, suspendedAction))
        }
        println("bar")
    }

    private fun startTimer() {
        println("foo")
        timeline = Timeline(
                KeyFrame(
                        Duration.seconds(HEARTBEAT_INTERVAL),
                        updateCoreState
                )
        )
        timeline!!.cycleCount = INDEFINITE
        timeline!!.play()
        println("bar")
    }


    private val updateCoreState = EventHandler<ActionEvent> {
        fire(CoreStateRequest)
    }
}