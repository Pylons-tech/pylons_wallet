package com.pylons.wallet.core.logging

import com.pylons.wallet.core.types.klaxon
import java.util.*

/**
 * Generic class for Logging Things.
 * Subclass this and override Logger.implementation to make walletcore use an implementation that
 * sends log files to the filesystem or over the network or w/e.
 */
open class Logger {
    /**
     * Models a single log entry.
     * evt is a string describing the event being logged (briefly),
     * tag is a string denoting the origin of the event (walletcore internals,
     * wallet implementation, malformed user data, etc.),
     * timestamp is a number of milliseconds since 1/1/1970.
     *
     * msg is the body of the entry, This should always be a JSON blob for parsing purposes.
     */
    data class Entry (
            val evt : String,
            val msg : String,
            val tag : String,
            val timestamp : Long = Date().time
    ) {
        /**
         * Emits the log entry serialized as a JSON string.
         */
        fun serialize () = klaxon.toJsonString(this)
    }

    companion object {
        /**
         * Gets a reference to a Logger instance.
         * If a wallet implementation defines its own Logger implementation,
         * you can set Logger.Implementation() to an instance of that class
         * to ensure walletcore actually uses it.
         */
        var implementation = Logger()
    }

    /**
     * Creates and logs a new entry using the given parameters. See Logger.Entry for more info.
     * This is an open method, and is intended to be overridden by Logger implementations that do something other than
     * pipe lones into stdout.
     */
    open fun log (evt: String, msg : String, tag : String) = println(Entry(evt, msg, tag))
}