package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.wallet.core.types.tx.msg.UpdateRecipe
import com.pylons.wallet.core.types.tx.recipe.*

val updateRecipeSignable = UpdateRecipe(
        id = "id001",
        blockInterval = 0,
        coinInputs = listOf(
                CoinInput("wood", 5)
        ),
        cookbookId = "name",
        description = "this has to meet character limits lol",
        entries = EntriesList(
                coinOutputs = listOf(
                        CoinOutput("0","chair", 1)
                ),
                itemModifyOutputs = listOf(ItemModifyOutput(0, listOf(), listOf(), listOf(), 0)),
                itemOutputs = listOf(
                        ItemOutput(
                                id = "1",
                                doubles = listOf(DoubleParam("1.0", "endurance",
                                        listOf(
                                                DoubleWeightRange("500.00", "100.00", 6),
                                                DoubleWeightRange("800.00", "501.00", 2)
                                        )
                                        , "")
                                ),
                                longs = listOf(
                                        LongParam("", "HP",
                                                listOf(
                                                        LongWeightRange(500, 100, 6),
                                                        LongWeightRange(800, 501, 2)
                                                )
                                                , "")
                                ),
                                strings = listOf(
                                        StringParam("1.0", "Name", "Raichu", "")
                                ),
                                transferFee = 0
                        )
                )
        ),
        outputs = listOf(
                WeightedOutput(
                        entryIds = listOf("0"), weight = "1"
                ),
                WeightedOutput(entryIds = listOf("1"),
                        weight = "1")
        ),
        itemInputs = listOf(
                ItemInput(
                        id = "",
                        doubles = listOf(),
                        longs = listOf(),
                        strings = listOf(
                                StringInputParam("Name", "Raichu")
                        ),
                        transferFee = FeeInputParam(0,0)
                )
        ),
        name = "recipeName",
        sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"
)