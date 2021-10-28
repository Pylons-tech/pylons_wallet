package tech.pylons.wallet.walletcore_test.fixtures

import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.msg.CreateRecipe
import tech.pylons.lib.types.tx.recipe.*

val createRecipeSignable = CreateRecipe(
        blockInterval = 0,
        coinInputs = listOf(
                CoinInput(listOf(Coin("wood", 5)))
        ),
        cookbookID = "id001",
        description = "this has to meet character limits lol",
        entries = EntriesList(
                coinOutputs = listOf(
                        CoinOutput("chair",Coin("upylon", 1), "1")
                ),
                itemModifyOutputs = listOf(),
                itemOutputs = listOf(
                        ItemOutput(
                                id = "Raichu",
                                doubles = listOf(DoubleParam("1.0",
                                        listOf(
                                                DoubleWeightRange("500.00", "100.00", 6),
                                                DoubleWeightRange("800.00", "501.00", 2)
                                        ), ""
                                )
                                ),
                                longs = listOf(
                                        LongParam("",
                                                listOf(
                                                        IntWeightRange(100, 500, 6),
                                                        IntWeightRange(100, 501, 2)
                                                ), ""
                                        )
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
        outputs = listOf(
                WeightedOutput(
                        entryIds = listOf("chair"), weight = 1
                ),
                WeightedOutput(entryIds = listOf("Raichu"),
                        weight = 1)
        ),
        itemInputs = listOf(
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
        name = "name",
        creator = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
        ID = "",
        extraInfo = "",
        version = "v1.1.0",
        enabled = true

)