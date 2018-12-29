package walletcore.ops

import walletcore.Core
import walletcore.constants.Keys
import walletcore.internal.bufferForeignProfile
import walletcore.internal.generateErrorMessageData
import walletcore.types.*

fun performTransaction (msg : MessageData) : Response {
    Core.foreignProfilesBuffer = setOf()
    val generalError = generateErrorMessageData(Error.MALFORMED_TX, "Could not generate a transaction from the supplied data.")
    if (!msg.strings.containsKey(Keys.otherProfileId)) return generateErrorMessageData(Error.MALFORMED_TX, "Did not provide an ID for remote profile involved in transaction.")
    if (bufferForeignProfile(msg.strings[Keys.otherProfileId]!!) == null) return generateErrorMessageData(Error.FOREIGN_PROFILE_DOES_NOT_EXIST, "No profile with provided ID exists.")
    val txDesc = TransactionDescription.fromMessageData(msg)
    return when (txDesc) {
        null -> return generalError
        else -> {
            val tx = Transaction.build(txDesc) ?: throw Exception("Failed to build transaction from TransactionDescription. This shouldn't happen; debug it.")
            val prf = Core.txHandler.commitTx(tx)
            val msg = when (prf) {
                null -> MessageData(booleans = mutableMapOf(Keys.success to false))
                else -> MessageData(booleans = mutableMapOf(Keys.success to true))
            }
            Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
        }
    }
}