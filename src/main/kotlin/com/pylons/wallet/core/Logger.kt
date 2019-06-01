package com.pylons.wallet.core

import com.squareup.moshi.Moshi
import java.util.*

open class Logger {
    private data class Entry (
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
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<Entry>(Entry::class.java)
        val sb = StringBuilder()
        val entry = Entry(msg, tag)
        sb.append(jsonAdapter.toJson(entry))
        return sb.toString()
    }
}