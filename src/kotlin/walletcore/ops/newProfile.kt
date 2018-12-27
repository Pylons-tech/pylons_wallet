package walletcore.ops

import walletcore.Core
import walletcore.constants.Keys
import walletcore.constants.ReservedKeys
import walletcore.internal.*
import walletcore.types.*
import java.lang.NullPointerException

internal fun newProfile (args : MessageData) : Response {
    val name = try {
        args.strings[Keys.name]
    }
    catch (e : NullPointerException) {
        return badArgs()
    }
    Core.cryptoHandler = Core.txHandler.getNewCryptoHandler()
    Core.cryptoHandler!!.newKeys()
    val id = Core.txHandler.getNewUserId()
    var msg : MessageData? = null
    Core.userProfile = Profile(id = id, strings = mapOf(ReservedKeys.profileName to name!!), provisional = true)
    Core.txHandler.registerNewProfile(ProfileRegisteredCallback{m -> msg = m})
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}

private class ProfileRegisteredCallback (val profileRetrieved : (MessageData) -> Unit) : Callback<Profile?> {
    override fun onSuccess(result: Profile?) {
        profileRetrieved(MessageData(booleans = mutableMapOf(Keys.profileExists to true)))
    }

    override fun onFailure(result: Profile?) {
        profileRetrieved(MessageData(booleans = mutableMapOf(Keys.profileExists to false)))
    }

    override fun onException(e: Exception?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}