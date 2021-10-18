package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.lib.types.tx.recipe.*

val basicItemOutput = ItemOutput(
        id = "",
        doubles = listOf(
                DoubleParam("1.0", "Heft", listOf(
                        DoubleWeightRange("1.0", "0.1", 1)
                ), "")
        ),
        longs = listOf(
                LongParam("1.0", "Level", listOf(
                        IntWeightRange(1, 100, 1)
                ), "")
        ),
        strings = listOf(
                StringParam("1.0", "Name", "fooBar", "")
        ),
        mutableStrings = listOf(),
        transferFee = listOf(),
        tradePercentage = "",
        quantity = 1,
        amountMinted = 1,
        tradeable = true
)