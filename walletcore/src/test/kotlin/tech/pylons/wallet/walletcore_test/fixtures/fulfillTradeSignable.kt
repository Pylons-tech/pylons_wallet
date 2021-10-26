package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.lib.types.tx.msg.FulfillTrade
import tech.pylons.lib.types.tx.recipe.PaymentInfo

val fulfillTradeSignable = FulfillTrade(
        ID = 1,
        Creator = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
        Items = listOf(),
        CoinInputsIndex = 0,
        paymentInfos = PaymentInfo("purchaseID", "processorName", "payerAddr", 1, "id", "signature")

)