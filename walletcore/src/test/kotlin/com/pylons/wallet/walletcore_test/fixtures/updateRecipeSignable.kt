package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.lib.types.tx.msg.UpdateRecipe
import com.pylons.lib.types.tx.recipe.*

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
                        CoinOutput("chair","chair", "1")
                ),
                itemModifyOutputs = null,
                itemOutputs = listOf(
                        ItemOutput(
                                id = "Raichu",
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
                                transferFee = 1232
                        )
                )
        ),
        outputs = listOf(
                WeightedOutput(
                        entryIds = listOf("chair"), weight = "1"
                ),
                WeightedOutput(entryIds = listOf("Raichu"),
                        weight = "1")
        ),
        itemInputs = listOf(
                ItemInput(
                        id = "Raichu",
                        conditions = ConditionList(listOf(), listOf(), listOf()),
                        doubles = listOf(),
                        longs = listOf(),
                        strings = listOf(
                                StringInputParam("Name", "Raichu")
                        ),
                        transferFee = FeeInputParam(0,10000)
                )
        ),
        name = "recipeName",
        sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"
)