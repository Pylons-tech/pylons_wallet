package com.pylons.wallet.core.internal

import com.pylons.wallet.core.types.*

/**
 * Generates the error message data to be given to clients in the
 * event that they attempt to call out to the wallet without
 * providing an action.
 */
internal fun noAction(): Response {
    return generateErrorMessageData(Error.NO_ACTION_FIELD, "The sent message didn't contain an action field, so the wallet was unable to resolve it.")
}