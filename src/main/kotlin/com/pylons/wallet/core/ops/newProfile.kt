package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.constants.ReservedKeys
import com.pylons.wallet.core.internal.*
import com.pylons.wallet.core.types.*
import kotlin.NullPointerException

internal fun newProfile (extraArgs : MessageData) : Response {
    require (extraArgs.strings.containsKey(Keys.NAME)) { badArgs() }
    val c = Core.engine.getNewCredentials()
    Core.setProfile(Profile(credentials =  c, strings = mutableMapOf(ReservedKeys.profileName to extraArgs.strings[Keys.NAME]!!), provisional = true,
            coins = mutableMapOf(), items = mutableListOf()))
    val tx = Core.newProfile(extraArgs.strings[Keys.NAME]!!)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(booleans = mutableMapOf(Keys.SUCCESS to true)), Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.newProfile (name : String) : Transaction = Core.engine.registerNewProfile(name).submit()