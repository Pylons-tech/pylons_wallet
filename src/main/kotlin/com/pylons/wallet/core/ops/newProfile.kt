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
    System.out.println("Name: ${name}")
    val c = Core.engine.getNewCredentials()
    Core.setProfile(Profile(credentials =  c, strings = mutableMapOf(ReservedKeys.profileName to name!!), provisional = true,
            coins = mutableMapOf(), items = mutableListOf()))
    val prf = Core.engine.registerNewProfile()
    val msg = when (prf) {
        null -> MessageData(booleans = mutableMapOf(Keys.profileExists to false))
        else -> MessageData(booleans = mutableMapOf(Keys.profileExists to true))
    }
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}