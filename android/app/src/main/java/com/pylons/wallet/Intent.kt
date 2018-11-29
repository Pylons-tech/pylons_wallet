package com.pylons.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import java.util.*
import kotlin.collections.ArrayList

class DeclaredKeysStorage {
    companion object {
        val declaredBooleansLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredBooleanArraysLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredBytesLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredByteArraysLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredCharsLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredCharArraysLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredDoublesLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredDoubleArraysLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredFloatsLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredFloatArraysLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredIntsLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredIntArraysLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredLongsLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredLongArraysLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredShortsLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredShortArraysLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredStringsLookup = mutableMapOf<Intent, MutableList<String>>()
        val declaredStringArraysLookup = mutableMapOf<Intent, MutableList<String>>()

        fun release (key : Intent) {
            declaredBooleansLookup.remove(key)
            declaredBooleanArraysLookup.remove(key)
            declaredBytesLookup.remove(key)
            declaredByteArraysLookup.remove(key)
            declaredCharsLookup.remove(key)
            declaredCharArraysLookup.remove(key)
            declaredDoublesLookup.remove(key)
            declaredDoubleArraysLookup.remove(key)
            declaredFloatsLookup.remove(key)
            declaredFloatArraysLookup.remove(key)
            declaredIntsLookup.remove(key)
            declaredIntArraysLookup.remove(key)
            declaredLongsLookup.remove(key)
            declaredLongArraysLookup.remove(key)
            declaredShortsLookup.remove(key)
            declaredShortArraysLookup.remove(key)
            declaredStringsLookup.remove(key)
            declaredStringArraysLookup.remove(key)
        }
    }
}

var Intent.declaredBooleans: MutableList<String>
get() {
    if (!DeclaredKeysStorage.declaredBooleansLookup.containsKey(this)) DeclaredKeysStorage.declaredBooleansLookup[this] = mutableListOf()
    return DeclaredKeysStorage.declaredBooleansLookup[this]!!
}
set(value) {
    DeclaredKeysStorage.declaredBooleansLookup[this] = value
}

var Intent.declaredBooleanArrays: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredBooleanArraysLookup.containsKey(this)) DeclaredKeysStorage.declaredBooleanArraysLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredBooleanArraysLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredBooleanArraysLookup[this] = value
    }

var Intent.declaredBytes: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredBytesLookup.containsKey(this)) DeclaredKeysStorage.declaredBytesLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredBytesLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredBytesLookup[this] = value
    }

var Intent.declaredByteArrays: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredByteArraysLookup.containsKey(this)) DeclaredKeysStorage.declaredByteArraysLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredByteArraysLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredByteArraysLookup[this] = value
    }

var Intent.declaredChars: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredCharsLookup.containsKey(this)) DeclaredKeysStorage.declaredCharsLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredCharsLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredCharsLookup[this] = value
    }

var Intent.declaredCharArrays: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredCharArraysLookup.containsKey(this)) DeclaredKeysStorage.declaredCharArraysLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredCharArraysLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredCharArraysLookup[this] = value
    }

var Intent.declaredDoubles: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredDoublesLookup.containsKey(this)) DeclaredKeysStorage.declaredDoublesLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredDoublesLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredDoublesLookup[this] = value
    }

var Intent.declaredDoubleArrays: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredDoubleArraysLookup.containsKey(this)) DeclaredKeysStorage.declaredDoubleArraysLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredDoubleArraysLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredCharArraysLookup[this] = value
    }

var Intent.declaredFloats: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredFloatsLookup.containsKey(this)) DeclaredKeysStorage.declaredFloatsLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredFloatsLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredFloatsLookup[this] = value
    }

var Intent.declaredFloatArrays: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredFloatArraysLookup.containsKey(this)) DeclaredKeysStorage.declaredFloatArraysLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredFloatArraysLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredFloatArraysLookup[this] = value
    }

var Intent.declaredInts: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredIntsLookup.containsKey(this)) DeclaredKeysStorage.declaredIntsLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredIntsLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredIntsLookup[this] = value
    }

var Intent.declaredIntArrays: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredIntArraysLookup.containsKey(this)) DeclaredKeysStorage.declaredIntArraysLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredIntArraysLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredIntArraysLookup[this] = value
    }

var Intent.declaredLongs: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredLongsLookup.containsKey(this)) DeclaredKeysStorage.declaredLongsLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredLongsLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredLongsLookup[this] = value
    }

var Intent.declaredLongArrays: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredLongArraysLookup.containsKey(this)) DeclaredKeysStorage.declaredLongArraysLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredLongArraysLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredLongArraysLookup[this] = value
    }

var Intent.declaredShorts: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredShortsLookup.containsKey(this)) DeclaredKeysStorage.declaredShortsLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredShortsLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredShortsLookup[this] = value
    }

var Intent.declaredShortArrays: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredShortArraysLookup.containsKey(this)) DeclaredKeysStorage.declaredShortArraysLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredShortArraysLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredShortArraysLookup[this] = value
    }

var Intent.declaredStrings: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredStringsLookup.containsKey(this)) DeclaredKeysStorage.declaredStringsLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredStringsLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredStringsLookup[this] = value
    }

var Intent.declaredStringArrays: MutableList<String>
    get() {
        if (!DeclaredKeysStorage.declaredStringArraysLookup.containsKey(this)) DeclaredKeysStorage.declaredStringArraysLookup[this] = mutableListOf()
        return DeclaredKeysStorage.declaredStringArraysLookup[this]!!
    }
    set(value) {
        DeclaredKeysStorage.declaredStringArraysLookup[this] = value
    }

fun Intent.putDeclaredExtra (name: String, value: Boolean) {
    putExtra(name, value)
    declaredBooleans.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: BooleanArray) {
    putExtra(name, value)
    declaredBooleanArrays.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: Byte) {
    putExtra(name, value)
    declaredBytes.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: ByteArray) {
    putExtra(name, value)
    declaredByteArrays.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: Char) {
    putExtra(name, value)
    declaredChars.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: CharArray) {
    putExtra(name, value)
    declaredCharArrays.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: Double) {
    putExtra(name, value)
    declaredDoubles.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: DoubleArray) {
    putExtra(name, value)
    declaredDoubleArrays.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: Float) {
    putExtra(name, value)
    declaredFloats.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: FloatArray) {
    putExtra(name, value)
    declaredFloatArrays.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: Int) {
    putExtra(name, value)
    declaredInts.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: IntArray) {
    putExtra(name, value)
    declaredIntArrays.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: Long) {
    putExtra(name, value)
    declaredLongs.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: LongArray) {
    putExtra(name, value)
    declaredLongArrays.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: Short) {
    putExtra(name, value)
    declaredShorts.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: ShortArray) {
    putExtra(name, value)
    declaredShortArrays.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: String) {
    putExtra(name, value)
    declaredStrings.add(name)
}

fun Intent.putDeclaredExtra (name: String, value: Array<String>) {
    putExtra(name, value)
    declaredStringArrays.add(name)
}

fun Intent.commitDeclaredExtras () {
    putExtra("_@PKEYS_BOOLEAN", declaredBooleans.toTypedArray())
    putExtra("_@PKEYS_BOOLEAN[]", declaredBooleanArrays.toTypedArray())
    putExtra("_@PKEYS_BYTE", declaredBytes.toTypedArray())
    putExtra("_@PKEYS_BYTE[]", declaredByteArrays.toTypedArray())
    putExtra("_@PKEYS_CHAR", declaredChars.toTypedArray())
    putExtra("_@PKEYS_CHAR[]", declaredCharArrays.toTypedArray())
    putExtra("_@PKEYS_DOUBLE", declaredBooleans.toTypedArray())
    putExtra("_@PKEYS_DOUBLE[]", declaredDoubleArrays.toTypedArray())
    putExtra("_@PKEYS_FLOAT", declaredFloats.toTypedArray())
    putExtra("_@PKEYS_FLOAT[]", declaredFloatArrays.toTypedArray())
    putExtra("_@PKEYS_INT", declaredInts.toTypedArray())
    putExtra("_@PKEYS_INT[]", declaredIntArrays.toTypedArray())
    putExtra("_@PKEYS_LONG", declaredLongs.toTypedArray())
    putExtra("_@PKEYS_LONG[]", declaredLongArrays.toTypedArray())
    putExtra("_@PKEYS_SHORT", declaredShorts.toTypedArray())
    putExtra("_@PKEYS_SHORT[]", declaredShortArrays.toTypedArray())
    putExtra("_@PKEYS_STRING", declaredStrings.toTypedArray())
    putExtra("_@PKEYS_STRING[]", declaredStringArrays.toTypedArray())
    DeclaredKeysStorage.release(this)
}