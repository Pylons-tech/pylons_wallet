package com.pylons.wallet.ipc

import kotlin.reflect.

import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.types.klaxon

fun deserializeUnknownMessage (json : String) : Message? {
    val jsonObject = klaxon.parser().parse(StringBuilder(json)) as JsonObject

    val mType = jsonObject.string("type")
    val mValue = jsonObject.obj("value")

    Message::class.sealedSubclasses.forEach {
        if (it.simpleName == mType) return mValue.
    }
}

private fun findParser () {

}