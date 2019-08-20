package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun updateCookbook(msg: MessageData): Response {
    if (msg.strings[Keys.ADDRESS] == null) throw BadMessageException("createCookbook", Keys.ADDRESS, "String")
    if (msg.strings[Keys.DEVELOPER] == null) throw BadMessageException("createCookbook", Keys.DEVELOPER, "String")
    if (msg.strings[Keys.DESCRIPTION] == null) throw BadMessageException("createCookbook", Keys.DESCRIPTION, "String")
    if (msg.strings[Keys.VERSION] == null) throw BadMessageException("createCookbook", Keys.VERSION, "String")
    if (msg.strings[Keys.SUPPORT_EMAIL] == null) throw BadMessageException("createCookbook", Keys.SUPPORT_EMAIL, "String")

    val tx = Core.updateCookbook(msg.strings[Keys.ADDRESS]!!, msg.strings["devel"]!!, msg.strings["desc"]!!,
            msg.strings["version"]!!, msg.strings["supportEmail"]!!)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(strings = mutableMapOf(Keys.TX to tx.id!!)), Status.OK_TO_RETURN_TO_CLIENT)}

fun Core.updateCookbook (id: String, devel : String, desc : String, version : String,
                         supportEmail : String) : Transaction =
        Core.engine.updateCookbook(id, devel, desc, version, supportEmail).submit()