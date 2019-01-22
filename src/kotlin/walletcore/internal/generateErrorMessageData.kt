package walletcore.internal

import walletcore.constants.*
import walletcore.types.*
import walletcore.Logger

/**
 * If an operation produces a (recoverable) error when we attempt to execute
 * it with the provided data, it generates a Response object containing
 * information on the error in question to be returned to the client.
 * Depending on the nature of the error, it may be possible for the client
 * to correct its own state and resubmit the original message, or it
 * may simply wind up giving an error message to end users.
 */
internal fun generateErrorMessageData(error: Error, info: String): Response {
    val msg = MessageData()
    msg.ints[Keys.errorCode] = error.value
    msg.strings[Keys.error] = error.name
    msg.strings[Keys.info] = info
    Logger.implementation.log(msg.errorToString(), LogTag.coreError)
    return Response(msg, Status.INCOMING_MESSAGE_MALFORMED)
}