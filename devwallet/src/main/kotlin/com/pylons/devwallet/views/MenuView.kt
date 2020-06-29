package com.pylons.devwallet.views

import tornadofx.*

class MenuView : View() {
    override val root = menubar {
        isUseSystemMenuBar = true
        menu("File") {
            item("Quit", "Shortcut+Q").action {
                println("Quitting!")
            }
        }
        menu("Edit") {
            item("Copy", "Shortcut+C").action {
                println("Copying!")
            }
            item("Paste", "Shortcut+V").action {
                println("Pasting!")
            }
        }
    }
}