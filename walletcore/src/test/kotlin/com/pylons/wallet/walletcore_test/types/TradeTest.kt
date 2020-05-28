package com.pylons.wallet.walletcore_test.types

import com.pylons.wallet.core.types.tx.Trade
import com.pylons.wallet.core.types.tx.item.Item
import com.pylons.wallet.core.types.tx.recipe.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TradeTest {

    private val tradeListJson = """
{
    "Trades": [{
            "ID": "cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e6050971e5d-7349-43a1-ab78-8a5e11a45988",
            "CoinInputs": null,
            "ItemInputs": [
                {
                    "Doubles": [
                        {
                            "Key": "XP",
                            "MinValue": "1",
                            "MaxValue": "1000000"
                        }
                    ],
                    "Longs": [
                        {
                            "Key": "level",
                            "MinValue": "1",
                            "MaxValue": "2"
                        }
                    ],
                    "Strings": [
                        {
                            "Key": "Name",
                            "Value": "LionBaby"
                        }
                    ]
                }
            ],
            "CoinOutputs": [
                {
                    "denom": "pylon",
                    "amount": "444"
                }
            ],
            "ItemOutputs": null,
            "ExtraInfo": "character buy request created by loud game",
            "Sender": "cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e60",
            "FulFiller": "cosmos1ng9ehpmjc4mk8s3e7jxfwa2t4rtkhyhj58ngrn",
            "Disabled": false,
            "Completed": true
        },
                {
                    "ID": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs401401a9-eb2f-41df-9480-1b3017dcd7f3",
                    "CoinInputs": [
                        {
                            "Coin": "pylon",
                            "Count": "40000"
                        }
                    ],
                    "ItemInputs": null,
                    "CoinOutputs": null,
                    "ItemOutputs": [
                        {
                            "ID": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs92340456-8623-44ca-8c5b-21eb779dca2a",
                            "Doubles": [
                                {
                                    "Key": "XP",
                                    "Value": "1"
                                }
                            ],
                            "Longs": [
                                {
                                    "Key": "level",
                                    "Value": "1"
                                },
                                {
                                    "Key": "GiantKill",
                                    "Value": "0"
                                },
                                {
                                    "Key": "Special",
                                    "Value": "0"
                                },
                                {
                                    "Key": "SpecialDragonKill",
                                    "Value": "0"
                                },
                                {
                                    "Key": "UndeadDragonKill",
                                    "Value": "0"
                                }
                            ],
                            "Strings": [
                                {
                                    "Key": "Name",
                                    "Value": "Tiger"
                                },
                                {
                                    "Key": "Type",
                                    "Value": "Character"
                                }
                            ],
                            "CookbookID": "LOUD-v0.1.0-1589853709",
                            "Sender": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
                            "OwnerRecipeID": "",
                            "Tradable": true,
                            "LastUpdate": "39334"
                        }
                    ],
                    "ExtraInfo": "character sell request created by loud game",
                    "Sender": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
                    "FulFiller": "cosmos1ng9ehpmjc4mk8s3e7jxfwa2t4rtkhyhj58ngrn",
                    "Disabled": false,
                    "Completed": true
                },
                        {
                            "ID": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs774d3f14-d988-4aaf-a51e-13e2ccd09221",
                            "CoinInputs": [
                                {
                                    "Coin": "pylon",
                                    "Count": "4000"
                                }
                            ],
                            "ItemInputs": null,
                            "CoinOutputs": null,
                            "ItemOutputs": [
                                {
                                    "ID": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs90580d1b-4521-466b-8e78-0f3b8e95f8f5",
                                    "Doubles": [
                                        {
                                            "Key": "attack",
                                            "Value": "3"
                                        }
                                    ],
                                    "Longs": [
                                        {
                                            "Key": "level",
                                            "Value": "1"
                                        },
                                        {
                                            "Key": "value",
                                            "Value": "100"
                                        }
                                    ],
                                    "Strings": [
                                        {
                                            "Key": "Name",
                                            "Value": "Wooden sword"
                                        }
                                    ],
                                    "CookbookID": "LOUD-v0.1.0-1589853709",
                                    "Sender": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
                                    "OwnerRecipeID": "",
                                    "Tradable": true,
                                    "LastUpdate": "39328"
                                }
                            ],
                            "ExtraInfo": "sword sell request created by loud game",
                            "Sender": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
                            "FulFiller": "cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e60",
                            "Disabled": false,
                            "Completed": true
                        },
                        {
                            "ID": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs7b0fc361-5ce0-4cfb-9173-039e84c89124",
                            "CoinInputs": null,
                            "ItemInputs": [
                                {
                                    "Doubles": null,
                                    "Longs": [
                                        {
                                            "Key": "level",
                                            "MinValue": "1",
                                            "MaxValue": "1"
                                        }
                                    ],
                                    "Strings": [
                                        {
                                            "Key": "Name",
                                            "Value": "Wooden sword"
                                        }
                                    ]
                                }
                            ],
                            "CoinOutputs": [
                                {
                                    "denom": "pylon",
                                    "amount": "333"
                                }
                            ],
                            "ItemOutputs": null,
                            "ExtraInfo": "sword buy request created by loud game",
                            "Sender": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
                            "FulFiller": "cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e60",
                            "Disabled": false,
                            "Completed": true
                        },
            {
                "ID": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxglb96ad4ee-3b7f-4b12-96c4-dca45556f812",
                "CoinInputs": null,
                "ItemInputs": [
                    {
                        "Doubles": null,
                        "Longs": null,
                        "Strings": [
                            {
                                "Key": "Name",
                                "Value": "Trading Knife v4"
                            }
                        ]
                    }
                ],
                "CoinOutputs": null,
                "ItemOutputs": [
                    {
                        "ID": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl2f92088b-1115-4b4e-8f20-5d1ee558af2a",
                        "Doubles": [
                            {
                                "Key": "attack",
                                "Value": "2"
                            }
                        ],
                        "Longs": [
                            {
                                "Key": "level",
                                "Value": "2"
                            }
                        ],
                        "Strings": [
                            {
                                "Key": "Name",
                                "Value": "Trading Knife v2"
                            }
                        ],
                        "CookbookID": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgldb212c3c-a228-4b62-b6bb-a48d0e75005a",
                        "Sender": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl",
                        "OwnerRecipeID": "",
                        "Tradable": true,
                        "LastUpdate": "125"
                    }
                ],
                "ExtraInfo": "item to item trading",
                "Sender": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl",
                "FulFiller": "cosmos1yafkzzx9lygqvy8hxx9l9e2cw7ct5rp3ly03ep",
                "Disabled": false,
                "Completed": true
            },
                    {
                        "ID": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl3da7b0cc-e79a-4e8b-90f0-3ce3032e691e",
                        "CoinInputs": null,
                        "ItemInputs": [
                            {
                                "Doubles": null,
                                "Longs": null,
                                "Strings": [
                                    {
                                        "Key": "Name",
                                        "Value": "Trading Knife v3"
                                    }
                                ]
                            }
                        ],
                        "CoinOutputs": [
                            {
                                "denom": "eugencoin",
                                "amount": "200"
                            }
                        ],
                        "ItemOutputs": null,
                        "ExtraInfo": "item to coin trading",
                        "Sender": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl",
                        "FulFiller": "cosmos1yafkzzx9lygqvy8hxx9l9e2cw7ct5rp3ly03ep",
                        "Disabled": false,
                        "Completed": true
                    },
    {
            "ID": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxglfde15ca3-1fa3-4608-ad7b-892bc0c15494",
            "CoinInputs": [
                {
                    "Coin": "pylon",
                    "Count": "1"
                }
            ],
            "ItemInputs": null,
            "CoinOutputs": null,
            "ItemOutputs": [
                {
                    "ID": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl55c90ca8-0566-499a-999f-753a13cc89c6",
                    "Doubles": [
                        {
                            "Key": "attack",
                            "Value": "1"
                        }
                    ],
                    "Longs": [
                        {
                            "Key": "level",
                            "Value": "1"
                        }
                    ],
                    "Strings": [
                        {
                            "Key": "Name",
                            "Value": "Trading Knife v1"
                        }
                    ],
                    "CookbookID": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgldb212c3c-a228-4b62-b6bb-a48d0e75005a",
                    "Sender": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl",
                    "OwnerRecipeID": "",
                    "Tradable": true,
                    "LastUpdate": "124"
                }
            ],
            "ExtraInfo": "coin to item trading",
            "Sender": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl",
            "FulFiller": "cosmos1yafkzzx9lygqvy8hxx9l9e2cw7ct5rp3ly03ep",
            "Disabled": false,
            "Completed": true
        },
    {
            "ID": "cosmos1mkk2q586y5pz263u5v8dv59723u58059ytprs961818c70-3c1c-48f6-9559-085b577af09e",
            "CoinInputs": [
                {
                    "Coin": "loudcoin",
                    "Count": "4000"
                }
            ],
            "ItemInputs": null,
            "CoinOutputs": [
                {
                    "denom": "pylon",
                    "amount": "330"
                }
            ],
            "ItemOutputs": null,
            "ExtraInfo": "created by loud game",
            "Sender": "cosmos1mkk2q586y5pz263u5v8dv59723u58059ytprs9",
            "FulFiller": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
            "Disabled": false,
            "Completed": true
        },
        {
            "ID": "cosmos1mkk2q586y5pz263u5v8dv59723u58059ytprs9ceed66cd-5fe2-480e-a4e7-64c728423b77",
            "CoinInputs": [
                {
                    "Coin": "pylon",
                    "Count": "50001"
                }
            ],
            "ItemInputs": null,
            "CoinOutputs": [
                {
                    "denom": "loudcoin",
                    "amount": "3000"
                }
            ],
            "ItemOutputs": null,
            "ExtraInfo": "created by loud game",
            "Sender": "cosmos1mkk2q586y5pz263u5v8dv59723u58059ytprs9",
            "FulFiller": "",
            "Disabled": false,
            "Completed": false
        },
        {
            "ID": "cosmos1ng9ehpmjc4mk8s3e7jxfwa2t4rtkhyhj58ngrn37ce48ae-4527-487d-b981-e40a63a18e5f",
            "CoinInputs": [
                {
                    "Coin": "loudcoin",
                    "Count": "332"
                }
            ],
            "ItemInputs": null,
            "CoinOutputs": [
                {
                    "denom": "pylon",
                    "amount": "33"
                }
            ],
            "ItemOutputs": null,
            "ExtraInfo": "created by loud game",
            "Sender": "cosmos1ng9ehpmjc4mk8s3e7jxfwa2t4rtkhyhj58ngrn",
            "FulFiller": "",
            "Disabled": false,
            "Completed": false
        }
    ]
}
    """.trimIndent()

    @Test
    fun getTradeList() {
        val expected = listOf(
                Trade("cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e6050971e5d-7349-43a1-ab78-8a5e11a45988", listOf(), listOf(ItemInput(listOf(DoubleInputParam("XP", "1", "1000000")), listOf(LongInputParam("level", 1, 2)), listOf(StringInputParam("Name", "LionBaby")))), listOf(CoinOutput("pylon", 444)), listOf(), "character buy request created by loud game", "cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e60", "cosmos1ng9ehpmjc4mk8s3e7jxfwa2t4rtkhyhj58ngrn", false, true
                ),
                Trade("cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs401401a9-eb2f-41df-9480-1b3017dcd7f3", listOf(CoinInput("pylon", 40000)), listOf(), listOf(), listOf(Item("cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs92340456-8623-44ca-8c5b-21eb779dca2a", "LOUD-v0.1.0-1589853709", "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs", "", true, 39334, mapOf("XP" to 1.0), mapOf("level" to 1L, "GiantKill" to 0L, "Special" to 0L, "SpecialDragonKill" to 0L, "UndeadDragonKill" to 0L), mapOf("Name" to "Tiger", "Type" to "Character"))), "character sell request created by loud game", "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs", "cosmos1ng9ehpmjc4mk8s3e7jxfwa2t4rtkhyhj58ngrn", false, true
                ),
                Trade("cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs774d3f14-d988-4aaf-a51e-13e2ccd09221", listOf(CoinInput("pylon", 4000)), listOf(), listOf(), listOf(Item("cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs90580d1b-4521-466b-8e78-0f3b8e95f8f5", "LOUD-v0.1.0-1589853709", "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs", "", true, 39328, mapOf("attack" to 3.0), mapOf("level" to 1L, "value" to 100L), mapOf("Name" to "Wooden sword"))), "sword sell request created by loud game", "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs", "cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e60", false, true
                ),
                Trade("cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs7b0fc361-5ce0-4cfb-9173-039e84c89124", listOf(), listOf(ItemInput(listOf(), listOf(LongInputParam("level", 1, 1)), listOf(StringInputParam("Name", "Wooden sword")))), listOf(CoinOutput("pylon", 333)), listOf(), "sword buy request created by loud game", "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs", "cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e60", false, true
                ),
                Trade("cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxglb96ad4ee-3b7f-4b12-96c4-dca45556f812", listOf(), listOf(ItemInput(listOf(), listOf(), listOf(StringInputParam("Name", "Trading Knife v4")))), listOf(), listOf(Item("cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl2f92088b-1115-4b4e-8f20-5d1ee558af2a", "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgldb212c3c-a228-4b62-b6bb-a48d0e75005a", "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl", "", true, 125, mapOf("attack" to 2.0), mapOf("level" to 2L), mapOf("Name" to "Trading Knife v2"))), "item to item trading", "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl", "cosmos1yafkzzx9lygqvy8hxx9l9e2cw7ct5rp3ly03ep", false, true
                ),
                Trade("cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl3da7b0cc-e79a-4e8b-90f0-3ce3032e691e", listOf(), listOf(ItemInput(listOf(), listOf(), listOf(StringInputParam("Name", "Trading Knife v3")))), listOf(CoinOutput("eugencoin", 200)), listOf(), "item to coin trading", "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl", "cosmos1yafkzzx9lygqvy8hxx9l9e2cw7ct5rp3ly03ep", false, true
                ),
                Trade("cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxglfde15ca3-1fa3-4608-ad7b-892bc0c15494", listOf(CoinInput("pylon", 1)), listOf(), listOf(), listOf(Item("cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl55c90ca8-0566-499a-999f-753a13cc89c6", "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgldb212c3c-a228-4b62-b6bb-a48d0e75005a", "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl", "", true, 124, mapOf("attack" to 1.0), mapOf("level" to 1L), mapOf("Name" to "Trading Knife v1"))), "coin to item trading", "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl", "cosmos1yafkzzx9lygqvy8hxx9l9e2cw7ct5rp3ly03ep", false, true
                ),
                Trade("cosmos1mkk2q586y5pz263u5v8dv59723u58059ytprs961818c70-3c1c-48f6-9559-085b577af09e", listOf(CoinInput("loudcoin", 4000)), listOf(), listOf(CoinOutput("pylon", 330)), listOf(), "created by loud game", "cosmos1mkk2q586y5pz263u5v8dv59723u58059ytprs9", "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs", false, true
                ),
                Trade("cosmos1mkk2q586y5pz263u5v8dv59723u58059ytprs9ceed66cd-5fe2-480e-a4e7-64c728423b77", listOf(CoinInput("pylon", 50001)), listOf(), listOf(CoinOutput("loudcoin", 3000)), listOf(), "created by loud game", "cosmos1mkk2q586y5pz263u5v8dv59723u58059ytprs9", "", false, false
                ),
                Trade("cosmos1ng9ehpmjc4mk8s3e7jxfwa2t4rtkhyhj58ngrn37ce48ae-4527-487d-b981-e40a63a18e5f", listOf(CoinInput("loudcoin", 332)), listOf(), listOf(CoinOutput("pylon", 33)), listOf(), "created by loud game", "cosmos1ng9ehpmjc4mk8s3e7jxfwa2t4rtkhyhj58ngrn", "", false, false
                ))
        Assertions.assertEquals(expected, Trade.listFromJson(tradeListJson))
    }
}