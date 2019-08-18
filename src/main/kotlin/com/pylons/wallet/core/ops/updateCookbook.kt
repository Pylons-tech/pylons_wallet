package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun updateCookbook(msg: MessageData): Response {
    if (msg.strings[Keys.id] == null) throw BadMessageException("createCookbook", Keys.id, "String")
    if (msg.strings["devel"] == null) throw BadMessageException("createCookbook", "devel", "String")
    if (msg.strings["desc"] == null) throw BadMessageException("createCookbook", "desc", "String")
    if (msg.strings["version"] == null) throw BadMessageException("createCookbook", "version", "String")
    if (msg.strings["supportEmail"] == null) throw BadMessageException("createCookbook", "supportEmail", "String")

    val tx = Core.updateCookbook(msg.strings[Keys.id]!!, msg.strings["devel"]!!, msg.strings["desc"]!!,
            msg.strings["version"]!!, msg.strings["supportEmail"]!!)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(strings = mutableMapOf(Keys.tx to tx.id!!)), Status.OK_TO_RETURN_TO_CLIENT)}

fun Core.updateCookbook (id: String, devel : String, desc : String, version : String,
                         supportEmail : String) : Transaction =
        Core.engine.updateCookbook(id, devel, desc, version, supportEmail).submit()