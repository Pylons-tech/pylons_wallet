package com.pylons.wallet.core.fixtures

import com.pylons.wallet.core.types.tx.msg.CreateRecipe
import com.pylons.wallet.core.types.tx.recipe.*

val createRecipeSignable = CreateRecipe(
        blockInterval = 0,
        coinInputs = listOf(
                CoinInput("wood", 5)
        ),
        cookbookId = "id001",
        description = "this has to meet character limits lol",
        entries = WeightedParamList(
                coinOutputs = listOf(
                        CoinOutput("chair", 1, 1)
                ),
                itemOutputs = listOf(
                        ItemOutput(
                                doubles = listOf(DoubleParam("1.0", "endurance",
                                        listOf(
                                                DoubleWeightRange("500.00", "100.00", 6),
                                                DoubleWeightRange("800.00", "501.00", 2)
                                        )
                                )
                                ),
                                longs = listOf(
                                        LongParam("", "HP",
                                                listOf(
                                                        LongWeightRange(500, 100, 6),
                                                        LongWeightRange(800, 501, 2)
                                                )
                                        )
                                ),
                                strings = listOf(
                                        StringParam("1.0", "Name", "Raichu")
                                ),
                                weight = 1
                        )
                )
        ),
        itemInputs = listOf(
                ItemInput(
                        doubles = listOf(),
                        longs = listOf(),
                        strings = listOf(
                                StringInputParam("Name", "Raichu")
                        )
                )
        ),
        name = "name",
        sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
        rType = 0,
        toUpgrade = ItemUpgradeParams(listOf(), listOf(), listOf())
)