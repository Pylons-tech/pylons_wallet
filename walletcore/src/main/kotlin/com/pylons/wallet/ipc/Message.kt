package com.pylons.wallet.ipc

/**
 * Message pipeline:
 * 1) match
 * 2) deserialize
 * 3) process
 * 4) (optional) ui
 * 5) resolve
 */
abstract class Message {
    abstract class Response {
        fun submit() {

        }
    }

    abstract fun deserialize(json : String) : Message

    abstract fun match(json: String)

    open fun ui () {}

    abstract fun resolve() : Response


}