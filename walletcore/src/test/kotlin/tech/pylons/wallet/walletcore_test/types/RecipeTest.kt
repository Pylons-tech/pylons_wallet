package tech.pylons.wallet.walletcore_test.types

import tech.pylons.lib.types.tx.recipe.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RecipeTest {

    private val sampleJson = """
        {
          "height": "0",
          "result": {
            "Recipes": [
              {
                "NodeVersion": "0.0.1",
                "ID": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt33709b48df5-2c27-4495-83d6-36e0771e3a94",
                "CookbookID": "-124015487",
                "Name": "RTEST_1596241180",
                "CoinInputs": [
                  {
                    "Coin": "pylon",
                    "Count": "1"
                  }
                ],
                "ItemInputs": null,
                "Entries": {
                  "CoinOutputs": null,
                  "ItemModifyOutputs": null,
                  "ItemOutputs": [
                    {
                      "ID": "itemMars",
                      "Doubles": [
                        {
                          "Rate": "1",
                          "Key": "Mass",
                          "WeightRanges": [
                            {
                              "Lower": "50",
                              "Upper": "100",
                              "Weight": 1
                            }
                          ],
                          "Program": ""
                        }
                      ],
                      "Longs": null,
                      "Strings": [
                        {
                          "Rate": "1",
                          "Key": "Name",
                          "Value": "Mars",
                          "Program": ""
                        }
                      ],
                      "TransferFee": 0
                    }
                  ]
                },
                "Outputs": [
                  {
                    "EntryIDs": [
                      "itemMars"
                    ],
                    "Weight": "1"
                  }
                ],
                "Description": "test recipe from test suite",
                "BlockInterval": "0",
                "Sender": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
                "Disabled": false
              }
            ]
          }
        }
    """.trimIndent()

    @Test
    fun parseJson() {
        val expected = listOf(Recipe(
                nodeVersion = "0.0.1",
                id = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt33709b48df5-2c27-4495-83d6-36e0771e3a94",
                cookbookId = "-124015487",
                name = "RTEST_1596241180",
                coinInputs = listOf(
                        CoinInput(
                            coin = "pylon",
                            count = 1
                        )
                ),
                itemInputs = listOf(),
                entries = EntriesList(
                        coinOutputs = listOf(),
                        itemModifyOutputs = listOf(),
                        itemOutputs = listOf(
                                ItemOutput(
                                        id = "itemMars",
                                        doubles = listOf(DoubleParam(
                                                rate = "1",
                                                key = "Mass",
                                                weightRanges = listOf(DoubleWeightRange(
                                                        lower = "50",
                                                        upper = "100",
                                                        weight = 1
                                                )),
                                                program = ""
                                        )),
                                        longs = listOf(),
                                        strings = listOf(StringParam(
                                                rate = "1",
                                                key = "Name",
                                                value = "Mars",
                                                program = ""
                                        )),
                                        transferFee = 0
                                )
                        )
                ),
                outputs = listOf(WeightedOutput(
                        entryIds = listOf("itemMars"),
                        weight = "1"
                )),
                description = "test recipe from test suite",
                blockInterval = 0,
                sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
                disabled = false,
                extraInfo = ""
        ))
        assertEquals(expected, Recipe.listFromJson(sampleJson))
    }
}