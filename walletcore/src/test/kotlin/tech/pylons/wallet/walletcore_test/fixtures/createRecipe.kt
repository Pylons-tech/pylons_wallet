package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.lib.types.tx.Coin
import tech.pylons.wallet.core.engine.TxPylonsDevEngine
import tech.pylons.lib.types.tx.recipe.*

fun emitCreateRecipe (engine : TxPylonsDevEngine, name : String, cbId : String, sender : String) = engine.createRecipe(
        creator = "pylo1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
        ID = "",
        blockInterval = 0,
        coinInputs = listOf(CoinInput(
                listOf(Coin("upylon",1))
        )),
        cookbookID = cbId,
        description = "test recipe from test suite",
        entries = EntriesList(
                coinOutputs = listOf(),
                itemModifyOutputs = listOf(),
                itemOutputs = listOf(
                        ItemOutput(
                                id = "itemMars",
                                doubles = listOf(
                                        DoubleParam(
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
                                                key = "Name",
                                                value = "Mars",
                                                program = ""
                                        )
                                ),
                                mutableStrings = listOf(
                                        StringKeyValue(
                                                Key = "Name",
                                                Value="nft_2"
                                        ),
                                ),
                                transferFee = listOf(Coin("upylon", 1)),
                                tradePercentage = "1000000000000000000",
                                quantity = 10,
                                amountMinted = 0,
                                tradeable = true
                        )
                )
        ),
        outputs = listOf(WeightedOutput(listOf("itemMars"), 1)),
        itemInputs = listOf(),
        name = name,
        version = "v1.1.0",
        extraInfo = "",
        enabled = true
)