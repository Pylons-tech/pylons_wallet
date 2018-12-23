package walletcore.ops

import walletcore.Core
import walletcore.types.*

internal fun getUserDetails(): Response {

        val msg = MessageData()
        when (Core.userProfile) {
            null -> msg.booleans["profileExists"] = false
            else -> Core.txHandler.getOwnBalances(BalancesRetrievedCallback(msg))
        }
        return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)

}

private class BalancesRetrievedCallback (val msg : MessageData)  : Callback<Profile?> {
    override fun onSuccess(result: Profile?) {
        val profile = Core.userProfile!!
        msg.booleans["profileExists"] = true
        val name = profile.getName()
        if (name != null) msg.strings["name"] = name
        msg.strings["id"] = profile.id
        msg.strings["coins"] = profile.coins.serializeCoins()
        msg.strings["items"] = profile.items.serialize()
        // and extras!
    }

    override fun onFailure(result: Profile?) {
        msg.booleans["profileExists"] = false
    }

    override fun onException(e: Exception?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}