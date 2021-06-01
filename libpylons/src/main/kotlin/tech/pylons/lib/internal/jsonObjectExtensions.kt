package tech.pylons.lib.internal

import com.beust.klaxon.JsonObject
import java.lang.ClassCastException

fun JsonObject.fuzzyDouble(key : String) : Double {
    return when (val v= this.getValue(key)) {
        is String -> this.string(key)!!.toDouble()
        is Number -> this.double(key)!!
        else -> throw ClassCastException("$v is not convertible to Double")
    }
}

fun JsonObject.fuzzyFloat(key : String) : Float {
    return when (val v= this.getValue(key)) {
        is String -> this.string(key)!!.toFloat()
        is Number -> this.float(key)!!
        else -> throw ClassCastException("$v is not convertible to Float")
    }
}

fun JsonObject.fuzzyInt(key : String) : Int {
    return when (val v= this.getValue(key)) {
        is String -> this.string(key)!!.toInt()
        is Number -> this.int(key)!!
        else -> throw ClassCastException("$v is not convertible to Int")
    }
}

fun JsonObject.fuzzyLong(key : String) : Long {
    return when (val v= this.getValue(key)) {
        is String -> this.string(key)!!.toLong()
        is Number -> this.long(key)!!
        else -> throw ClassCastException("$v is not convertible to Long")
    }
}