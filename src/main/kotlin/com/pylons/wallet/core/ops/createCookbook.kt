package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun createCookbook(msg: MessageData): Response {
    if (msg.strings[Keys.name] == null) throw BadMessageException("createCookbook", Keys.name, "String")
    if (msg.strings["devel"] == null) throw BadMessageException("createCookbook", "devel", "String")
    if (msg.strings["desc"] == null) throw BadMessageException("createCookbook", "desc", "String")
    if (msg.strings["version"] == null) throw BadMessageException("createCookbook", "version", "String")
    if (msg.strings["supportEmail"] == null) throw BadMessageException("createCookbook", "supportEmail", "String")
    if (msg.ints["level"] == null) throw BadMessageException("createCookbook", "level", "Int")

    val tx = Core.engine.createCookbook(msg.strings[Keys.name]!!, msg.strings["devel"]!!, msg.strings["desc"]!!,
            msg.strings["version"]!!, msg.strings["supportEmail"]!!, msg.ints["level"]!!)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(strings = mutableMapOf(Keys.tx to tx.id!!)), Status.OK_TO_RETURN_TO_CLIENT)}

fun Core.createCookbook (name : String, devel : String, desc : String, version : String,
                         supportEmail : String, level : Int) : Transaction =
        Core.engine.createCookbook(name, devel, desc, version, supportEmail, level).submit()