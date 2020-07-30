package com.pylons.wallet.walletcore_test.fixtures

import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.types.tx.recipe.*
import java.time.Instant

fun emitUpdateRecipe (engine: TxPylonsDevEngine, name : String, cbId : String, rId : String, sender : String) =
        engine.updateRecipe (
        blockInterval = 1,
        coinInputs = listOf(CoinInput(
                coin = "pylon",
                count = 10
        )),
        cookbookId = cbId,
        description = "test recipe from test suite",
        entries = EntriesList(
                coinOutputs = listOf(),
                itemModifyOutputs = listOf(),
                itemOutputs = listOf(
                        ItemOutput(
                                doubles = listOf(
                                        DoubleParam(
                                                rate = "1",
                                                key = "Mass",
                                                weightRanges = listOf(
                                                        DoubleWeightRange("200", "100", 1)
                                                ),
                                                program = ""
                                        )
                                ),
                                longs = listOf(),
                                strings = listOf(
                                        StringParam(
                                                rate = "1",
                                                key = "Name",
                                                value = "Earth",
                                                program = ""
                                        )
                                ),
                                transferFee = 0
                        )
                )
        ),
        outputs = listOf(WeightedOutput(listOf(0), "1")),
        itemInputs = listOf(),
        name = name,
        sender = sender,
        id = rId
)