package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.lib.types.tx.msg.GoogleIapGetPylons

val googleIapGetPylonsSignable = GoogleIapGetPylons(
        productId = "your.product.id",
        purchaseToken = "your.purchase.token",
        receiptDataBase64 = "your.receipt.data",
        signature = "your.purchase.signature",
        sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337")
