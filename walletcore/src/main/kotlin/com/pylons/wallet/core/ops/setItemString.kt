package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core

fun Core.setItemString (itemId : String, field : String, value : String) =
        engine.setItemFieldString(itemId, field, value)