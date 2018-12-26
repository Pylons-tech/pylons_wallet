package walletcore.ops

import walletcore.Core
import walletcore.types.*

internal fun getUserDetails(): Response {
        var msg = MessageData()
        when (Core.userProfile) {
            null -> msg.booleans["profileExists"] = false
            else -> Core.txHandler.getOwnBalances(BalancesRetrievedCallback{m : MessageData -> msg = m})
        }
        return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}

private class BalancesRetrievedCallback (val messageRetrieved : (MessageData) -> Unit)  : Callback<Profile?> {
    override fun onSuccess(result: Profile?) {
        val profile = Core.userProfile!!
        messageRetrieved(profile.detailsToMessageData())
    }

    override fun onFailure(result: Profile?) {
        val msg = MessageData()
        msg.booleans["profileExists"] = false
        messageRetrieved(msg)
    }

    override fun onException(e: Exception?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}