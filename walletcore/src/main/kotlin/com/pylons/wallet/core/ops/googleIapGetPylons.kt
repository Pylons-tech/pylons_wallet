package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.lib.types.types.*

fun Core.googleIapGetPylons (productId: String, purchaseToken : String, receiptData : String,
                             signature : String) : Transaction = engine.googleIapGetPylons(productId,
        purchaseToken, receiptData, signature).submit()