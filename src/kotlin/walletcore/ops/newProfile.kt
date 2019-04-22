package walletcore.ops

import walletcore.Core
import walletcore.constants.Keys
import walletcore.constants.ReservedKeys
import walletcore.internal.*
import walletcore.types.*
import kotlin.NullPointerException

internal fun newProfile (extraArgs : MessageData) : Response {
    val name = try {
        extraArgs.strings[Keys.name]
    }
    catch (e : NullPointerException) {
        return badArgs()
    }
    Core.cryptoHandler = Core.txHandler.getNewCryptoHandler()
    Core.cryptoHandler!!.newKeys()
    val id = Core.txHandler.getNewUserId()
    Core.setProfile(Profile(id = id, strings = mutableMapOf(ReservedKeys.profileName to name!!), provisional = true))
    val prf = Core.txHandler.registerNewProfile()
    val msg = when (prf) {
        null -> MessageData(booleans = mutableMapOf(Keys.profileExists to false))
        else -> MessageData(booleans = mutableMapOf(Keys.profileExists to true))
    }
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}