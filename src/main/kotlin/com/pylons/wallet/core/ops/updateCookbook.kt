package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun updateCookbook(msg: MessageData): Response {
    checkValid(msg)
    val tx = Core.updateCookbook(
            id = msg.strings[Keys.ID]!!,
            developer = msg.strings[Keys.DEVELOPER]!!,
            description = msg.strings[Keys.DESCRIPTION]!!,
            version = msg.strings[Keys.VERSION]!!,
            supportEmail = msg.strings[Keys.SUPPORT_EMAIL]!!)
    waitUntilCommitted(tx.id!!)
    val outgoingMessage = MessageData(
            strings = mutableMapOf(
                    Keys.TX to tx.id!!
            )
    )
    return Response(outgoingMessage, Status.OK_TO_RETURN_TO_CLIENT)
}

private fun checkValid(msg : MessageData) {
    require (msg.strings.containsKey(Keys.ID)) { throw BadMessageException("updateCookbook", Keys.ID, "String") }
    require (msg.strings.containsKey(Keys.DEVELOPER)) { throw BadMessageException("updateCookbook", Keys.DEVELOPER, "String") }
    require (msg.strings.containsKey(Keys.DESCRIPTION)) { throw BadMessageException("updateCookbook", Keys.DESCRIPTION, "String") }
    require (msg.strings.containsKey(Keys.VERSION)) { throw BadMessageException("updateCookbook", Keys.VERSION, "String") }
    require (msg.strings.containsKey(Keys.SUPPORT_EMAIL)) { throw BadMessageException("updateCookbook", Keys.SUPPORT_EMAIL, "String") }
}

fun Core.updateCookbook (id: String, developer : String, description : String, version : String,
                         supportEmail : String) : Transaction =
        engine.updateCookbook(id, developer, description, version, supportEmail).submit()