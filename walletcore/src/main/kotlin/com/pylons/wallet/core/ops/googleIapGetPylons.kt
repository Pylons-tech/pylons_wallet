package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*

fun Core.googleIapGetPylons (productId: String, purchaseToken : String, receiptDataBase64 : String,
                             signature : String) : Transaction = engine.googleIapGetPylons(productId,
        purchaseToken, receiptDataBase64, signature).submit()