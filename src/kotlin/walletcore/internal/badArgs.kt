package walletcore.internal

import walletcore.types.*

internal fun badArgs(): Response {
    return generateErrorMessageData(Error.BAD_ARGUMENTS, "The wallet application generated malformed extra" +
            "arguments for the requested action.")
}