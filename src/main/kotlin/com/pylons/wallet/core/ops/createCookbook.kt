package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun createCookbook(msg: MessageData): Response {
    if (msg.strings[Keys.NAME] == null) throw BadMessageException("createCookbook", Keys.NAME, "String")
    if (msg.strings[Keys.DEVELOPER] == null) throw BadMessageException("createCookbook", Keys.DEVELOPER, "String")
    if (msg.strings[Keys.DESCRIPTION] == null) throw BadMessageException("createCookbook", Keys.DESCRIPTION, "String")
    if (msg.strings[Keys.VERSION] == null) throw BadMessageException("createCookbook", Keys.VERSION, "String")
    if (msg.strings[Keys.SUPPORT_EMAIL] == null) throw BadMessageException("createCookbook", Keys.SUPPORT_EMAIL, "String")
    if (msg.ints[Keys.LEVEL] == null) throw BadMessageException("createCookbook", Keys.LEVEL, "Int")

    val tx = Core.createCookbook(msg.strings[Keys.NAME]!!, msg.strings["devel"]!!, msg.strings["desc"]!!,
            msg.strings["version"]!!, msg.strings["supportEmail"]!!, msg.ints["level"]!!)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(strings = mutableMapOf(Keys.TX to tx.id!!)), Status.OK_TO_RETURN_TO_CLIENT)}

fun Core.createCookbook (name : String, devel : String, desc : String, version : String,
                         supportEmail : String, level : Int) : Transaction =
        Core.engine.createCookbook(name, devel, desc, version, supportEmail, level).submit()