package walletcore.ops

import walletcore.Core
import walletcore.constants.Keys
import walletcore.internal.bufferForeignProfile
import walletcore.internal.generateErrorMessageData
import walletcore.types.*

fun performTransaction (args : MessageData) : Response {
    val generalError = generateErrorMessageData(Error.MALFORMED_TX, "Could not generate a transaction from the supplied data.")
    if (!args.strings.containsKey(Keys.otherProfileId)) return generateErrorMessageData(Error.MALFORMED_TX, "Did not provide an ID for remote profile involved in transaction.")
    if (bufferForeignProfile(args.strings[Keys.otherProfileId]!!) == null) return generateErrorMessageData(Error.FOREIGN_PROFILE_DOES_NOT_EXIST, "No profile with provided ID exists.")
    val txDesc = TransactionDescription.fromMessageData(args)
    return when (txDesc) {
        null -> return generalError
        else -> {
            val tx = Transaction.build(txDesc)
            var msg : MessageData? = null
            Core.txHandler.commitTx(tx!!, TxCallback{m -> msg = m}) // this isn't v. safe, TODO: fix it
            Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
        }
    }
}

private class TxCallback (val txProcessed : (MessageData) -> Unit) : Callback<Profile?> {
    override fun onSuccess(result: Profile?) {
        txProcessed(MessageData(booleans = mutableMapOf(Keys.success to true)))
    }

    override fun onFailure(result: Profile?) {
        txProcessed(MessageData(booleans = mutableMapOf(Keys.success to false)))
    }

    override fun onException(e: Exception?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}