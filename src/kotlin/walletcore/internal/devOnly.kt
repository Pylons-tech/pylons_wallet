package walletcore.internal

import walletcore.Core
import walletcore.types.Response

internal fun devOnly(func: () -> Response): Response {
    if (!Core.txHandler.isDevTxLayer) throw Exception("This action is forbidden on non-dev TX layers.")
    return func()
}