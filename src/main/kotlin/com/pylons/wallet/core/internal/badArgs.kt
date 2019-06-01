package com.pylons.wallet.core.internal

import com.pylons.wallet.core.types.*

internal fun badArgs(): Response {
    return generateErrorMessageData(Error.BAD_ARGUMENTS, "The wallet application generated malformed extra" +
            "arguments for the requested action.")
}