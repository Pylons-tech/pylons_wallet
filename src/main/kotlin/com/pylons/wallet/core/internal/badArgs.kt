package com.pylons.wallet.core.internal

import com.pylons.wallet.core.types.*

internal fun badArgs(): Response {
    throw IllegalArgumentException("The wallet application generated malformed extra" +
            "arguments for the requested action.")
}