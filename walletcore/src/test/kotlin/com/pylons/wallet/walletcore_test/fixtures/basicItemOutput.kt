package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.wallet.core.types.tx.recipe.*

val basicItemOutput = ItemOutput(
        doubles = listOf(
                DoubleParam("1.0", "Heft", listOf(
                        DoubleWeightRange("1.0", "0.1", 1)
                ), "")
        ),
        longs = listOf(
                LongParam("1.0", "Level", listOf(
                        LongWeightRange(100, 1, 1)
                ), "")
        ),
        strings = listOf(
                StringParam("1.0", "Name", "fooBar", "")
        ),
        modifyItem = ItemUpgradeParams(listOf(), listOf(), listOf(), 0),
        transferFee = 0
)