package com.pylons.wallet.core.internal

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Response

internal fun devOnly(func: () -> Response): Response {
    if (!Core.engine.isDevEngine) throw Exception("This action is forbidden on non-dev TX layers.")
    return func()
}