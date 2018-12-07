package com.pylons.wallet.app

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import walletCore.MessageData

class CoreUtil {
    companion object {
        internal fun copyMessageDataToIntent (msg : MessageData, intent : Intent) {
            /**
             * TODO: once the shim is redesigned to use the same strategy we use to do this w/o
             * declared extras between wallet/core this needs to stop making the putDeclaredExtra calls
             */
            for (k in msg.booleans.keys) intent.putDeclaredExtra(k, msg.booleans[k]!!)
            for (k in msg.booleanArrays.keys) intent.putDeclaredExtra(k, msg.booleanArrays[k]!!)
            for (k in msg.bytes.keys) intent.putDeclaredExtra(k, msg.bytes[k]!!)
            for (k in msg.byteArrays.keys) intent.putDeclaredExtra(k, msg.byteArrays[k]!!)
            for (k in msg.chars.keys) intent.putDeclaredExtra(k, msg.chars[k]!!)
            for (k in msg.charArrays.keys) intent.putDeclaredExtra(k, msg.charArrays[k]!!)
            for (k in msg.doubles.keys) intent.putDeclaredExtra(k, msg.doubles[k]!!)
            for (k in msg.doubleArrays.keys) intent.putDeclaredExtra(k, msg.doubleArrays[k]!!)
            for (k in msg.floats.keys) intent.putDeclaredExtra(k, msg.floats[k]!!)
            for (k in msg.floatArrays.keys) intent.putDeclaredExtra(k, msg.floatArrays[k]!!)
            for (k in msg.ints.keys) intent.putDeclaredExtra(k, msg.ints[k]!!)
            for (k in msg.intArrays.keys) intent.putDeclaredExtra(k, msg.intArrays[k]!!)
            for (k in msg.longs.keys) intent.putDeclaredExtra(k, msg.longs[k]!!)
            for (k in msg.longArrays.keys) intent.putDeclaredExtra(k, msg.longArrays[k]!!)
            for (k in msg.shorts.keys) intent.putDeclaredExtra(k, msg.shorts[k]!!)
            for (k in msg.shortArrays.keys) intent.putDeclaredExtra(k, msg.shortArrays[k]!!)
            for (k in msg.strings.keys) intent.putDeclaredExtra(k, msg.strings[k]!!)
            for (k in msg.stringArrays.keys) intent.putDeclaredExtra(k, msg.stringArrays[k]!!.toTypedArray())
            intent.commitDeclaredExtras()
        }
    }
}

