package walletcore.internal

import walletcore.types.*

/**
 * Generates the error message data to be given to clients in the
 * event that they attempt to call out to the wallet, but provide
 * an unrecognized action.
 */
internal fun unrecognizedAction(action : String): Response {
    return generateErrorMessageData("UNREGOGNIZED_ACTION_FIELD", "The sent message contained an unrecognized action ($action)," +
            " so the wallet was unable to resolve it.")
}