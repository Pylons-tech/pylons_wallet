package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import java.lang.Exception

internal fun waitUntilCommitted (txHash : String) {
    var tries = 0
    val maxTries = 32
    try {
        Core.engine.getTransaction(txHash)
    } catch (e : Exception) {
        while (tries < maxTries) {
            tries++
            try {
                Core.engine.getTransaction(txHash)
            } catch (e : Exception) {
                // swallow, we're just retrying anyway, there's prolly a less clumsy way to do this
            }
        }
        if (tries >= maxTries) throw e
    }
}