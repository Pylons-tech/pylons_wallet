package com.pylons.wallet.core.internal

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*

/**
 *
 */
internal fun retryOnError(func: () -> Response): Response {
    var r : Response? = null
    for (i in (0..Core.rejectedTxRetryTimes)) {
        r = func()
        if (!r.msg!!.isError()) break
        runBlocking { delay(Core.retryDelay) }
    }
    return r!!
}