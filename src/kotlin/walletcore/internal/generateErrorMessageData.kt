package walletcore.internal

import walletcore.types.*

/**
 * If an operation produces a (recoverable) error when we attempt to execute
 * it with the provided data, it generates a Response object containing
 * information on the error in question to be returned to the client.
 * Depending on the nature of the error, it may be possible for the client
 * to correct its own state and resubmit the original message, or it
 * may simply wind up giving an error message to end users.
 */
internal fun generateErrorMessageData(error: String, info: String): Response {
    val msg = MessageData()
    msg.strings["error"] = error
    msg.strings["info"] = info
    return Response(msg, Status.INCOMING_MESSAGE_MALFORMED)
}