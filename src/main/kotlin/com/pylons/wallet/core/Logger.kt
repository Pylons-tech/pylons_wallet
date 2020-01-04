package com.pylons.wallet.core

import com.beust.klaxon.Klaxon
import java.util.*

open class Logger {
    private val klaxon = Klaxon()
    data class Entry (
            val msg : String,
            val tag : String,
            val timestamp : Long = Date().time
    )

    companion object {
        var implementation = Logger()
    }

    open fun log (msg : String, tag : String) {
        System.out.println(getLogLine(msg, tag))
    }

    fun getLogLine (msg : String, tag : String) : String {
        val sb = StringBuilder()
        val entry = Entry(msg, tag)
        sb.append(klaxon.toJsonString(entry))
        return sb.toString()
    }
}