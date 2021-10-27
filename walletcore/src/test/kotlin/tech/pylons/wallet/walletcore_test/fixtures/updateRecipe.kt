package tech.pylons.wallet.walletcore_test.fixtures

import jdk.jfr.Enabled
import junit.runner.Version
import tech.pylons.lib.types.tx.Coin
import tech.pylons.wallet.core.engine.TxPylonsDevEngine
import tech.pylons.lib.types.tx.recipe.*

fun emitUpdateRecipe (engine: TxPylonsDevEngine, name : String, cbId : String, rId : String, creator : String) =
        engine.updateRecipe (
                Creator = creator,
        BlockInterval = 1,
        CoinInputs = listOf(CoinInput(
                listOf(Coin("upylon", 10))
        )),
        CookbookID = cbId,
        Description = "test recipe from test suite",
                Version = "v1.1.0",
        Entries = EntriesList(
                coinOutputs = listOf(),
                itemModifyOutputs = listOf(),
                itemOutputs = listOf(
                        ItemOutput(
                                id = "itemEarth",
                                doubles = listOf(
                                        DoubleParam(
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
                                                key = "Name",
                                                value = "Earth",
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
                        tradePercentage = "10%",
                        quantity = 10,
                        amountMinted = 2,
                        tradeable = true
                        )
                )
        ),
        Outputs = listOf(WeightedOutput(listOf("itemEarth"), 1)),
        ItemInputs = listOf(),
        Name = name,
        ID = rId,
                Enabled = true,
        ExtraInfo = ""
)