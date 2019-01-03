package walletcore.internal

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import walletcore.Core
import walletcore.types.*

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