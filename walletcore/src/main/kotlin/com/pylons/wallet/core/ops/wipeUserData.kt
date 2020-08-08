package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core

fun Core.wipeUserData () {
    tearDown()
    onWipeUserData?.invoke()
}