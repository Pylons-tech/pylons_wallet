package com.pylons.wallet.core.internal

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Response

/**
 * actionResolutionTable helper method. Indicates that an action should be limited to dev-use engines,
 * and throws an exception if a non-dev engine attempts to handle dev-only action types.
 */
internal fun devOnly(func: () -> Response): Response {
    if (!Core.engine.isDevEngine) throw Exception("This action is forbidden on non-dev TX layers.")
    return func()
}