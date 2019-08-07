package com.pylons.wallet.core.engine

import com.pylons.wallet.core.types.Backend

internal class TxPylonsDevEngine : TxPylonsEngine () {
    override val isDevEngine: Boolean = true
    override val backendType: Backend = Backend.LIVE_DEV
}