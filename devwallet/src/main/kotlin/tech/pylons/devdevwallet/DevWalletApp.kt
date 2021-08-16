package tech.pylons.devdevwallet

import javafx.application.Application
import tornadofx.*

@ExperimentalUnsignedTypes
class DevWalletApp : App(MainView::class, Styles::class) {
    init {
        // We don't need to do anything with CoreThread immediately, but
        // we want the initializer to run

    }

}

@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
    Application.launch(DevWalletApp::class.java, *args)
}