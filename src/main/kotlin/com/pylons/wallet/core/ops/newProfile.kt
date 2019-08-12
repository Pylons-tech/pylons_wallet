package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.constants.ReservedKeys
import com.pylons.wallet.core.internal.*
import com.pylons.wallet.core.types.*
import kotlin.NullPointerException

internal fun newProfile (extraArgs : MessageData) : Response {
    val name = try {
        extraArgs.strings[Keys.name]
    }
    catch (e : NullPointerException) {
        return badArgs()
    }
    val c = Core.engine.getNewCredentials()
    Core.setProfile(Profile(credentials =  c, strings = mutableMapOf(ReservedKeys.profileName to name!!), provisional = true,
            coins = mutableMapOf(), items = mutableListOf()))
    val tx = Core.newProfile(extraArgs.strings[Keys.name]!!)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(booleans = mutableMapOf(Keys.success to true)), Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.newProfile (name : String) : Transaction = Core.engine.registerNewProfile(name).submit()