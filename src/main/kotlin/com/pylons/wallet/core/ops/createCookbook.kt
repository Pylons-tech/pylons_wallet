package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun createCookbook(msg: MessageData): Response {
    checkValid(msg)
    val tx = Core.createCookbook(
            name = msg.strings[Keys.NAME]!!,
            developer = msg.strings[Keys.DEVELOPER]!!,
            description = msg.strings[Keys.DESCRIPTION]!!,
            version = msg.strings[Keys.VERSION]!!,
            supportEmail = msg.strings[Keys.SUPPORT_EMAIL]!!,
            level = msg.longs[Keys.LEVEL]!!,
            costPerBlock = msg.longs[Keys.COST_PER_BLOCK]!!
    )
    waitUntilCommitted(tx.id!!)
    val outgoingMessage = MessageData(
            strings = mutableMapOf(
                    Keys.TX to tx.id!!
            )
    )
    return Response(outgoingMessage, Status.OK_TO_RETURN_TO_CLIENT)
}

private fun checkValid(msg : MessageData) {
    require (msg.strings.containsKey(Keys.NAME)) { throw BadMessageException("createCookbook", Keys.NAME, "String") }
    require (msg.strings.containsKey(Keys.DEVELOPER)) { throw BadMessageException("createCookbook", Keys.DEVELOPER, "String") }
    require (msg.strings.containsKey(Keys.DESCRIPTION)) { throw BadMessageException("createCookbook", Keys.DESCRIPTION, "String") }
    require (msg.strings.containsKey(Keys.VERSION)) { throw BadMessageException("createCookbook", Keys.VERSION, "String") }
    require (msg.strings.containsKey(Keys.SUPPORT_EMAIL)) { throw BadMessageException("createCookbook", Keys.SUPPORT_EMAIL, "String") }
    require (msg.longs.containsKey(Keys.LEVEL)) { throw BadMessageException("createCookbook", Keys.LEVEL, "Long") }
    require (msg.longs.containsKey(Keys.COST_PER_BLOCK)) { throw BadMessageException("createCookbook", Keys.COSTPERBLOCK, "Long") }
}

fun Core.createCookbook (name : String, developer : String, description : String, version : String,
                         supportEmail : String, level : Long, costPerBlock : Long) : Transaction =
        engine.createCookbook(name, developer, description, version, supportEmail, level, costPerBlock).submit()