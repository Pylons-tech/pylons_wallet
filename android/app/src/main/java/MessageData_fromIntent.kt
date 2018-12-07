package com.pylons.wallet.app

import android.content.Intent
import walletCore.MessageData

fun messageDataFromIntent(intent : Intent) : MessageData {
    val msg = MessageData()
    val ks = intent.extras.keySet()
    for (key in ks) {
        val v = intent.extras.get(key)
        when (v) {
            is Boolean -> msg.booleans[key] = v
            is BooleanArray -> msg.booleanArrays[key] = v
            is Byte -> msg.bytes[key] = v
            is ByteArray -> msg.byteArrays[key] = v
            is Char -> msg.chars[key] = v
            is CharArray -> msg.charArrays[key] = v
            is Double -> msg.doubles[key] = v
            is DoubleArray -> msg.doubleArrays[key] = v
            is Float -> msg.floats[key] = v
            is FloatArray -> msg.floatArrays[key] = v
            is Int -> msg.ints[key] = v;
            is IntArray -> msg.intArrays[key] = v
            is Long -> msg.longs[key] = v
            is LongArray -> msg.longArrays[key] = v
            is Short -> msg.shorts[key] = v
            is ShortArray -> msg.shortArrays[key] = v
            is String -> msg.strings[key] = v
            is Array<*> -> if (v[0] is String) msg.stringArrays[key] = (v as Array<String>).toMutableList()
        }
    }
    return msg
}