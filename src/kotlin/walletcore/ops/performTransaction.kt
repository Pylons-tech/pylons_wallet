package walletcore.ops

import walletcore.Core
import walletcore.constants.Keys
import walletcore.types.*

fun performTransaction (args : MessageData) : Response {
    val txDesc = TransactionDescription.fromMessageData(args)
    val tx = Transaction.build(txDesc!!)
    var msg : MessageData? = null
    Core.txHandler.commitTx(tx!!, TxCallback{m -> msg = m}) // this isn't v. safe
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
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