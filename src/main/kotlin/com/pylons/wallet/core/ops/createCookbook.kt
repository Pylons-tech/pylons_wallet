package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun createCookbook(msg: MessageData): Response {
    require (msg.strings.containsKey(Keys.NAME)) { throw BadMessageException("createCookbook", Keys.NAME, "String") }
    require (msg.strings.containsKey(Keys.DEVELOPER)) { throw BadMessageException("createCookbook", Keys.DEVELOPER, "String") }
    require (msg.strings.containsKey(Keys.DESCRIPTION)) { throw BadMessageException("createCookbook", Keys.DESCRIPTION, "String") }
    require (msg.strings.containsKey(Keys.VERSION)) { throw BadMessageException("createCookbook", Keys.VERSION, "String") }
    require (msg.strings.containsKey(Keys.SUPPORT_EMAIL)) { throw BadMessageException("createCookbook", Keys.SUPPORT_EMAIL, "String") }
    require (msg.ints.containsKey(Keys.LEVEL)) { throw BadMessageException("createCookbook", Keys.LEVEL, "Int") }

    val tx = Core.createCookbook(msg.strings[Keys.NAME]!!, msg.strings["devel"]!!, msg.strings["desc"]!!,
            msg.strings["version"]!!, msg.strings["supportEmail"]!!, msg.ints["level"]!!)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(strings = mutableMapOf(Keys.TX to tx.id!!)), Status.OK_TO_RETURN_TO_CLIENT)}

fun Core.createCookbook (name : String, devel : String, desc : String, version : String,
                         supportEmail : String, level : Int) : Transaction =
        Core.engine.createCookbook(name, devel, desc, version, supportEmail, level).submit()