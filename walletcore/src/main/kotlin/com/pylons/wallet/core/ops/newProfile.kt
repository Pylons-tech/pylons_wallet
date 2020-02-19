package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.constants.ReservedKeys
import com.pylons.wallet.core.internal.*
import com.pylons.wallet.core.types.*

internal fun newProfile (msg : MessageData) : Response {
    // Ensure we have required keys - strings["NAME"]
    require (msg.strings.containsKey(Keys.NAME)) {  throw BadMessageException("newProfile", Keys.NAME, "String") }
    // Generate new credentials and configure user profile for them
    val c = Core.engine.getNewCredentials()
    Core.setProfile(
            Profile(
                    credentials =  c,
                    strings = mutableMapOf(ReservedKeys.profileName to msg.strings[Keys.NAME]!!),
                    provisional = true,
                    coins = listOf()
            )
    )
    // Give the supplied arguments to the core and run newprofile command, then wait on tx commit
    val tx = Core.newProfile(msg.strings[Keys.NAME]!!)
    waitUntilCommitted(tx.id!!)
    // Build outgoing message
    val outgoingMessage = MessageData(
            strings = mutableMapOf(
                    Keys.TX to tx.id!!
            )
    )
    // Return to client
    return Response(outgoingMessage, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.newProfile (name : String, kp : SECP256K1.KeyPair? = null) : Transaction = engine.registerNewProfile(name, kp).submit()