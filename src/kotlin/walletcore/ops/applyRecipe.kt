package walletcore.ops

import walletcore.Core
import walletcore.Logger
import walletcore.constants.Keys
import walletcore.constants.LogTag
import walletcore.internal.*
import walletcore.tx.TxHandler
import walletcore.types.*

internal fun applyRecipe (msg: MessageData) : Response {
    Logger.implementation.log(msg.strings[Keys.recipe].orEmpty(), LogTag.info)
    Logger.implementation.log(msg.strings[Keys.cookbook].orEmpty(), LogTag.info)
    var error : Response? = null
    if (!isValid(msg){error = it}) return error!!
    val preferredItemIds = msg.stringArrays[Keys.preferredItemIds] ?: mutableListOf()
    val msg = when (Core.txHandler.applyRecipe(msg.strings[Keys.cookbook]!!, msg.strings[Keys.recipe]!!, preferredItemIds.toList())) {
        null -> MessageData(booleans = mutableMapOf(Keys.success to false))
        else -> MessageData(booleans = mutableMapOf(Keys.success to true))
    }
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}

private fun isValid (msg : MessageData, handleError : (Response) -> Unit) : Boolean {
    System.out.println("A")
    if (!msg.strings.containsKey(Keys.cookbook)) { handleError(generateErrorMessageData(Error.MALFORMED_TX,
            "Did not provide an ID for required cookbook.")); return false }
    System.out.println("B")
    if (!msg.strings.containsKey(Keys.recipe)) { handleError(generateErrorMessageData(Error.MALFORMED_TX,
            "Did not provide an ID for required recipe.")); return false }
    return true
}