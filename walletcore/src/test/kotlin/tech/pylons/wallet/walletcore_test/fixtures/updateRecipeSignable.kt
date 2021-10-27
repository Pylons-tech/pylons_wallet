package tech.pylons.wallet.walletcore_test.fixtures

import jdk.jfr.Enabled
import junit.runner.Version
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.msg.UpdateRecipe
import tech.pylons.lib.types.tx.recipe.*

val updateRecipeSignable = UpdateRecipe(
        ID = "id001",
        BlockInterval = 0,
        CoinInputs = listOf(
                CoinInput(listOf(Coin("wood", 5)))
        ),
        CookbookID = "name",
        Description = "this has to meet character limits lol",
        Entries = EntriesList(
                coinOutputs = listOf(
                        CoinOutput("chair",Coin("chair", 1), "1")
                ),
                itemModifyOutputs = listOf(),
                itemOutputs = listOf(
                        ItemOutput(
                                id = "Raichu",
                                doubles = listOf(DoubleParam("1.0",
                                        listOf(
                                                DoubleWeightRange("500.00", "100.00", 6),
                                                DoubleWeightRange("800.00", "501.00", 2)
                                        )
                                        , "")
                                ),
                                longs = listOf(
                                        LongParam("",
                                                listOf(
                                                        IntWeightRange(100, 500, 6),
                                                        IntWeightRange(501, 800, 2)
                                                )
                                                , "")
                                ),
                                strings = listOf(
                                        StringParam("1.0", "Name", "Raichu")
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
        Outputs = listOf(
                WeightedOutput(
                        entryIds = listOf("chair"), weight = 1
                ),
                WeightedOutput(entryIds = listOf("Raichu"),
                        weight = 1)
        ),
        ItemInputs = listOf(
                ItemInput(
                        id = "Raichu",
                        conditions = ConditionList(listOf(), listOf(), listOf()),
                        doubles = listOf(),
                        longs = listOf(),
                        strings = listOf(
                                StringInputParam("Name", "Raichu")
                        )
                )
        ),
        Name = "recipeName",
        Creator = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
        Version = "v1.1.0",
        Enabled = true,
        ExtraInfo = ""
)