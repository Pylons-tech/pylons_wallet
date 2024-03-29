package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.wallet.core.engine.TxPylonsDevEngine
import tech.pylons.lib.types.tx.recipe.*

fun emitCreateRecipe (engine : TxPylonsDevEngine, name : String, cbId : String, sender : String) = engine.createRecipe(
        blockInterval = 0,
        coinInputs = listOf(CoinInput(
                coin = "pylon",
                count = 1
        )),
        cookbookId = cbId,
        description = "test recipe from test suite",
        entries = EntriesList(
                coinOutputs = listOf(),
                itemModifyOutputs = listOf(),
                itemOutputs = listOf(
                        ItemOutput(
                                id = "itemMars",
                                doubles = listOf(
                                        DoubleParam(
                                                rate = "1",
                                                key = "Mass",
                                                weightRanges = listOf(
                                                        DoubleWeightRange("100", "50", 1)
                                                ),
                                                program = ""
                                        )
                                ),
                                longs = listOf(),
                                strings = listOf(
                                        StringParam(
                                                rate = "1",
                                                key = "Name",
                                                value = "Mars",
                                                program = ""
                                        )
                                ),
                                transferFee = 0
                        )
                )
        ),
        outputs = listOf(WeightedOutput(listOf("itemMars"), "1")),
        itemInputs = listOf(),
        name = name,
        extraInfo = ""
)