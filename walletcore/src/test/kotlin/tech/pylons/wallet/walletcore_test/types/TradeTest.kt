package tech.pylons.wallet.walletcore_test.types

import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.Trade
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.lib.types.tx.trade.TradeItemInput
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TradeTest {

    private val tradeListJson = """
{
    "height": "0",
    "result": {
    "Trades": [{
            "NodeVersion": "0.0.1",
            "ID": "cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e6050971e5d-7349-43a1-ab78-8a5e11a45988",
            "CoinInputs": null,
            "ItemInputs": [
                {
                    "CookbookID": "LOUD-v0.1.0-1589853709",
                    "ItemInput": {
                        "ID": "",
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
                        ],
                        "TransferFee": {
                            "MinValue": "0",
                            "MaxValue": "0"
                        }
                    }
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
                    "NodeVersion": "0.0.1",
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
                            "NodeVersion": "0.0.1",
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
                            "OwnerRecipeID": "orid",
                            "OwnerTradeID": "otid",
                            "Tradable": true,
                            "LastUpdate": "39334",
                            "TransferFee": "1"
                        }
                    ],
                    "ExtraInfo": "character sell request created by loud game",
                    "Sender": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
                    "FulFiller": "cosmos1ng9ehpmjc4mk8s3e7jxfwa2t4rtkhyhj58ngrn",
                    "Disabled": false,
                    "Completed": true
                },
                        {
                            "NodeVersion": "0.0.1",
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
                                    "NodeVersion": "0.0.1",
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
                                    "OwnerTradeID": "",
                                    "Tradable": true,
                                    "LastUpdate": "39328",
                                    "TransferFee": "0"
                                }
                            ],
                            "ExtraInfo": "sword sell request created by loud game",
                            "Sender": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
                            "FulFiller": "cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e60",
                            "Disabled": false,
                            "Completed": true
                        },
                        {
                            "NodeVersion": "0.0.1",
                            "ID": "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs7b0fc361-5ce0-4cfb-9173-039e84c89124",
                            "CoinInputs": null,
                            "ItemInputs": [
                                {
                                    "CookbookID": "LOUD-v0.1.0-1589853709",
                                    "ItemInput": {
                                        "ID": "",
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
                                        ],
                                        "TransferFee": {
                                            "MinValue": "0",
                                            "MaxValue": "0"
                                        }
                                    }
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
                "NodeVersion": "0.0.1",
                "ID": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxglb96ad4ee-3b7f-4b12-96c4-dca45556f812",
                "CoinInputs": null,
                "ItemInputs": [
                    {
                        "CookbookID": "LOUD-v0.1.0-1589853709",
                        "ItemInput": {
                            "ID": "",
                            "Doubles": null,
                            "Longs": null,
                            "Strings": [
                                {
                                    "Key": "Name",
                                    "Value": "Trading Knife v4"
                                }
                            ],
                            "TransferFee": {
                                "MinValue": "0",
                                "MaxValue": "0"
                            }
                        }
                    }
                ],
                "CoinOutputs": null,
                "ItemOutputs": [
                    {
                        "NodeVersion": "0.0.1",
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
                        "OwnerTradeID":"",
                        "Tradable": true,
                        "LastUpdate": "125",
                        "TransferFee": "0"
                    }
                ],
                "ExtraInfo": "item to item trading",
                "Sender": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl",
                "FulFiller": "cosmos1yafkzzx9lygqvy8hxx9l9e2cw7ct5rp3ly03ep",
                "Disabled": false,
                "Completed": true
            },
                    {
                        "NodeVersion": "0.0.1",
                        "ID": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl3da7b0cc-e79a-4e8b-90f0-3ce3032e691e",
                        "CoinInputs": null,
                        "ItemInputs": [
                            {
                                "CookbookID": "LOUD-v0.1.0-1589853709",
                                "ItemInput": {
                                    "ID": "KNIFE",
                                    "Doubles": null,
                                    "Longs": null,
                                    "Strings": [
                                        {
                                            "Key": "Name",
                                            "Value": "Trading Knife v3"
                                        }
                                    ],
                                    "TransferFee": {
                                        "MinValue": "50",
                                        "MaxValue": "100"
                                    }
                                }
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
            "NodeVersion": "0.0.1",
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
                    "NodeVersion": "0.0.1",
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
                    "OwnerTradeID":"",
                    "Tradable": true,
                    "LastUpdate": "124",
                    "TransferFee": "0"
                }
            ],
            "ExtraInfo": "coin to item trading",
            "Sender": "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl",
            "FulFiller": "cosmos1yafkzzx9lygqvy8hxx9l9e2cw7ct5rp3ly03ep",
            "Disabled": false,
            "Completed": true
        },
    {
            "NodeVersion": "0.0.1",
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
            "NodeVersion": "0.0.1",
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
            "NodeVersion": "0.0.1",
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
}
    """.trimIndent()

    @Test
    fun getTradeList() {
        val expected = listOf(
                Trade(
                    nodeVersion = "0.0.1",
                    id = "cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e6050971e5d-7349-43a1-ab78-8a5e11a45988",
                    coinInputs = listOf(),
                    itemInputs = listOf(TradeItemInput(
                        "LOUD-v0.1.0-1589853709",
                        ItemInput(
                            "",
                            conditions = ConditionList(listOf(), listOf(), listOf()),
                            doubles = listOf(DoubleInputParam("XP", "1", "1000000")),
                            longs = listOf(LongInputParam("level", 1, 2)),
                            strings = listOf(StringInputParam("Name", "LionBaby")),
                            transferFee = FeeInputParam(0, 0)
                        )
                    )),
                    coinOutputs = listOf(Coin("pylon", 444)),
                    itemOutputs = listOf(),
                    extraInfo = "character buy request created by loud game",
                    sender = "cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e60",
                    fulfiller = "cosmos1ng9ehpmjc4mk8s3e7jxfwa2t4rtkhyhj58ngrn",
                    disabled = false,
                    completed = true
                ),
                Trade(
                    nodeVersion = "0.0.1",
                    id = "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs401401a9-eb2f-41df-9480-1b3017dcd7f3",
                    coinInputs = listOf(CoinInput("pylon", 40000)),
                    itemInputs = listOf(),
                    coinOutputs = listOf(),
                    itemOutputs = listOf(Item(
                        nodeVersion = "0.0.1",
                        id = "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs92340456-8623-44ca-8c5b-21eb779dca2a",
                        cookbookId = "LOUD-v0.1.0-1589853709",
                        sender = "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
                        ownerRecipeID = "orid",
                        ownerTradeID = "otid",
                        tradable = true,
                        lastUpdate = 39334,
                        doubles = mapOf("XP" to "1.0"),
                        longs = mapOf(
                            "level" to "1L",
                            "GiantKill" to "0L",
                            "Special" to "0L",
                            "SpecialDragonKill" to "0L",
                            "UndeadDragonKill" to "0L"),
                        strings = mapOf("Name" to "Tiger", "Type" to "Character"),
                        transferFee = 1
                    )),
                    extraInfo = "character sell request created by loud game",
                    sender = "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
                    fulfiller = "cosmos1ng9ehpmjc4mk8s3e7jxfwa2t4rtkhyhj58ngrn",
                    disabled = false,
                    completed = true
                ),
                Trade(
                    nodeVersion = "0.0.1",
                    id = "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs774d3f14-d988-4aaf-a51e-13e2ccd09221",
                    coinInputs = listOf(CoinInput("pylon", 4000)),
                    itemInputs = listOf(),
                    coinOutputs = listOf(),
                    itemOutputs = listOf(Item(
                        nodeVersion = "0.0.1",
                        id = "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs90580d1b-4521-466b-8e78-0f3b8e95f8f5",
                        cookbookId = "LOUD-v0.1.0-1589853709",
                        sender = "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
                        ownerRecipeID = "",
                        ownerTradeID = "",
                        tradable = true,
                        lastUpdate = 39328,
                        doubles = mapOf("attack" to "3.0"),
                        longs = mapOf("level" to "1L", "value" to "100L"),
                        strings = mapOf("Name" to "Wooden sword"),
                        transferFee = 0
                    )),
                    extraInfo = "sword sell request created by loud game",
                    sender = "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
                    fulfiller = "cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e60",
                    disabled = false,
                    completed = true
                ),
                Trade(
                    nodeVersion = "0.0.1",
                    id = "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs7b0fc361-5ce0-4cfb-9173-039e84c89124",
                    coinInputs = listOf(),
                    itemInputs = listOf(TradeItemInput(cookbookId = "LOUD-v0.1.0-1589853709",
                        itemInput = ItemInput(
                            id = "",
                            conditions = ConditionList(listOf(), listOf(), listOf()),
                            doubles = listOf(),
                            longs = listOf(LongInputParam("level", 1, 1)),
                            strings = listOf(StringInputParam("Name", "Wooden sword")),
                            transferFee = FeeInputParam(0, 0)
                        )
                    )),
                    coinOutputs = listOf(Coin("pylon", 333)),
                    itemOutputs = listOf(),
                    extraInfo = "sword buy request created by loud game",
                    sender = "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
                    fulfiller = "cosmos16hwvr9u0zrwu2smaefylq2t8nsfz3g6ytd9e60",
                    disabled = false,
                    completed = true
                ),
                Trade(
                    nodeVersion = "0.0.1",
                    id = "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxglb96ad4ee-3b7f-4b12-96c4-dca45556f812",
                    coinInputs = listOf(),
                    itemInputs = listOf(TradeItemInput(cookbookId = "LOUD-v0.1.0-1589853709",
                        itemInput = ItemInput(
                            id = "",
                            conditions = ConditionList(listOf(), listOf(), listOf()),
                            doubles = listOf(),
                            longs = listOf(),
                            strings = listOf(StringInputParam("Name", "Trading Knife v4")),
                            transferFee = FeeInputParam(0, 0)
                        )
                    )),
                    coinOutputs = listOf(),
                    itemOutputs = listOf(Item(
                        nodeVersion = "0.0.1",
                        id = "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl2f92088b-1115-4b4e-8f20-5d1ee558af2a",
                        cookbookId = "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgldb212c3c-a228-4b62-b6bb-a48d0e75005a",
                        sender = "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl",
                        ownerRecipeID = "",
                        ownerTradeID = "",
                        tradable = true,
                        lastUpdate = 125,
                        doubles = mapOf("attack" to "2.0"),
                        longs = mapOf("level" to "2L"),
                        strings = mapOf("Name" to "Trading Knife v2"),
                        transferFee = 0
                    )),
                    extraInfo = "item to item trading",
                    sender = "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl",
                    fulfiller = "cosmos1yafkzzx9lygqvy8hxx9l9e2cw7ct5rp3ly03ep",
                    disabled = false,
                    completed = true
                ),
                Trade(
                    nodeVersion = "0.0.1",
                    id = "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl3da7b0cc-e79a-4e8b-90f0-3ce3032e691e",
                    coinInputs = listOf(),
                    itemInputs = listOf(TradeItemInput(cookbookId = "LOUD-v0.1.0-1589853709",
                        itemInput = ItemInput(
                            id = "KNIFE",
                            conditions = ConditionList(listOf(), listOf(), listOf()),
                            doubles = listOf(),
                            longs = listOf(),
                            strings = listOf(StringInputParam("Name", "Trading Knife v3")),
                            transferFee = FeeInputParam(50, 100)
                        )
                    )),
                    coinOutputs = listOf(Coin("eugencoin", 200)),
                    itemOutputs = listOf(),
                    extraInfo = "item to coin trading",
                    sender = "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl",
                    fulfiller = "cosmos1yafkzzx9lygqvy8hxx9l9e2cw7ct5rp3ly03ep",
                    disabled = false,
                    completed = true
                ),
                Trade(
                    nodeVersion = "0.0.1",
                    id = "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxglfde15ca3-1fa3-4608-ad7b-892bc0c15494",
                    coinInputs = listOf(CoinInput("pylon", 1)),
                    itemInputs = listOf(),
                    coinOutputs = listOf(),
                    itemOutputs = listOf(Item(
                        nodeVersion = "0.0.1",
                        id = "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl55c90ca8-0566-499a-999f-753a13cc89c6",
                        cookbookId = "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgldb212c3c-a228-4b62-b6bb-a48d0e75005a",
                        sender = "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl",
                        ownerRecipeID = "",
                        ownerTradeID = "",
                        tradable = true,
                        lastUpdate = 124,
                        doubles = mapOf("attack" to "1.0"),
                        longs = mapOf("level" to "1L"),
                        strings = mapOf("Name" to "Trading Knife v1"),
                        transferFee = 0
                    )),
                    extraInfo = "coin to item trading",
                    sender = "cosmos1h7pam0zqguvrnkqyf4hlwj0jg3xpwkq0u0uxgl",
                    fulfiller = "cosmos1yafkzzx9lygqvy8hxx9l9e2cw7ct5rp3ly03ep",
                    disabled = false,
                    completed = true
                ),
                Trade(
                    nodeVersion = "0.0.1",
                    id = "cosmos1mkk2q586y5pz263u5v8dv59723u58059ytprs961818c70-3c1c-48f6-9559-085b577af09e",
                    coinInputs = listOf(CoinInput("loudcoin", 4000)),
                    itemInputs = listOf(),
                    coinOutputs = listOf(Coin("pylon", 330)),
                    itemOutputs = listOf(),
                    extraInfo = "created by loud game",
                    sender = "cosmos1mkk2q586y5pz263u5v8dv59723u58059ytprs9",
                    fulfiller = "cosmos1842mp69ydaljedffpnatcjtl7ns23vf6mzptfs",
                    disabled = false,
                    completed = true
                ),
                Trade(
                    nodeVersion = "0.0.1",
                    id = "cosmos1mkk2q586y5pz263u5v8dv59723u58059ytprs9ceed66cd-5fe2-480e-a4e7-64c728423b77",
                    coinInputs = listOf(CoinInput("pylon", 50001)),
                    itemInputs = listOf(),
                    coinOutputs = listOf(Coin("loudcoin", 3000)),
                    itemOutputs = listOf(),
                    extraInfo = "created by loud game",
                    sender = "cosmos1mkk2q586y5pz263u5v8dv59723u58059ytprs9",
                    fulfiller = "",
                    disabled = false,
                    completed = false
                ),
                Trade(
                    nodeVersion = "0.0.1",
                    id = "cosmos1ng9ehpmjc4mk8s3e7jxfwa2t4rtkhyhj58ngrn37ce48ae-4527-487d-b981-e40a63a18e5f",
                    coinInputs = listOf(CoinInput("loudcoin", 332)),
                    itemInputs = listOf(),
                    coinOutputs = listOf(Coin("pylon", 33)),
                    itemOutputs = listOf(),
                    extraInfo = "created by loud game",
                    sender = "cosmos1ng9ehpmjc4mk8s3e7jxfwa2t4rtkhyhj58ngrn",
                    fulfiller = "",
                    disabled = false,
                    completed = false
                ))
        Assertions.assertEquals(expected, Trade.listFromJson(tradeListJson))
    }
}