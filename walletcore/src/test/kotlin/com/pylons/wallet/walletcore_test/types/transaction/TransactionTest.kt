package com.pylons.wallet.walletcore_test.types.transaction

import com.pylons.lib.types.Transaction
import com.pylons.lib.types.tx.*
import com.pylons.lib.types.tx.msg.ExecuteRecipe
import com.pylons.wallet.core.internal.ProtoJsonUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TransactionTest {
    private val executeRecipeResponse = """
        {
          "height": "24295",
          "txhash": "706D5C01916B0243CE44C99BE2C27CEC38B9BF60BBEA9B17C7EFF7A4370DE9A0",
          "data": "7B224D657373616765223A227375636365737366756C6C792065786563757465642074686520726563697065222C22537461747573223A2253756363657373222C224F7574707574223A225733736964486C775A534936496B4E50535534694C434A6A62326C75496A6F69624739315A474E76615734694C434A6862573931626E51694F6A4973496D6C305A57314A5243493649694A394C48736964486C775A534936496B6C55525530694C434A6A62326C75496A6F69496977695957317664573530496A6F774C434A7064475674535551694F694A6A62334E7462334D78633367346432317359323033624464796457786E4E325A6862545532626D64345A3255305A6E4E32654845334E6E45794F474D774E6A4533597A6C6C4D4330774E32466B4C5451774E5449744F545A684D43316B4D54517A597A526A5A5441784E5759696656303D227D",
          "raw_log": "[{\"msg_index\":\"0\",\"success\":true,\"log\":\"\"}]",
          "logs": [
            {
              "msg_index": "0",
              "success": true,
              "log": ""
            }
          ],
          "gas_wanted": "200000",
          "gas_used": "53272",
          "tags": [
            {
              "key": "action",
              "value": "execute_recipe"
            }
          ],
          "tx": {
            "type": "auth/StdTx",
            "value": {
              "msg": [
                {
                  "type": "pylons/ExecuteRecipe",
                  "value": {
                    "RecipeID": "LOUD-hunt-rabbits-with-no-weapon-recipe-v0.1.0-1589223853",
                    "Sender": "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c",
                    "ItemIDs": [
                      "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c0617c9e0-07ad-4052-96a0-d143c4ce015f"
                    ]
                  }
                }
              ],
              "fee": {
                "amount": null,
                "gas": "200000"
              },
              "signatures": [
                {
                  "pub_key": {
                    "type": "tendermint/PubKeySecp256k1",
                    "value": "AyO+7DL1dj4hfvZaDkG1nr2rHtmHkU2W9/sppgsM5Suu"
                  },
                  "signature": "4UMXYbcgpIwEGCXvxe87coRw+1UatkbH9S8XogXo9ugQqE35WLNd23hZ77cke3gnncdf0b61diHAWd9kYo3keg=="
                }
              ],
              "memo": ""
            }
          },
          "timestamp": "2020-05-17T20:40:41Z"
        }
    """.trimIndent()

    private val errorResponse = """
        {
          "height": "17819",
          "txhash": "BFF5DE310A37F0B3A0E550A40074478824CC00D8760EAF28B378DAF91E6BCA03",
          "codespace": "sdk",
          "code": 18,
          "raw_log": "invalid request: The recipe doesn't exist: failed to execute message; message index: 0",
          "gas_wanted": "400000",
          "gas_used": "20423",
          "tx": {
            "type": "cosmos-sdk/StdTx",
            "value": {
              "msg": [
                {
                  "type": "pylons/ExecuteRecipe",
                  "value": {
                    "RecipeID": "LOUD-get-character-recipe-v0.1.0-1589853708",
                    "Sender": "cosmos1cmdcfat6n8vhlysnlzyqsnlty2wrkx05uyp7ez",
                    "ItemIDs": null
                  }
                }
              ],
              "fee": {
                "amount": [],
                "gas": "400000"
              },
              "signatures": [
                {
                  "pub_key": {
                    "type": "tendermint/PubKeySecp256k1",
                    "value": "AiRKdkdNgsMV6k21jFD1Wswyow0raUpx/gC+jE5v1STP"
                  },
                  "signature": "WzdzUlobvGaTkr6Y7SdaqWelmY12ERsWeNwKhzpAtnwBgz+aIhV0hMJsV61MdqpwlAz1epY5QZvL306By87dLQ=="
                }
              ],
              "memo": ""
            }
          },
          "timestamp": "2020-07-20T22:00:08Z"
        }
    """.trimIndent()

    private val checkExecutionResponse = """
        {
          "height": "2920",
          "txhash": "A85DCD7536C991657DD7D352760A6D0C18A8A7C634B33C7EDCC8CB2C9324E40C",
          "data": "7B224D657373616765223A227375636365737366756C6C7920636F6D706C657465642074686520657865637574696F6E222C22537461747573223A2253756363657373222C224F7574707574223A225733736964486C775A534936496B6C55525530694C434A6A62326C75496A6F69496977695957317664573530496A6F774C434A7064475674535551694F694A6A62334E7462334D786554683265584E6E4F576874646D46326132523463485A6A59335979646D557A626E4E7A646A5668646D30776133517A4D7A646A4D4463314F446C684E53316C5A5752684C5451335A446374595467775A5331695A6A59784E6A55795A4441794E574D696656303D227D",
          "raw_log": "[{\"msg_index\":0,\"log\":\"\",\"events\":[{\"type\":\"message\",\"attributes\":[{\"key\":\"action\",\"value\":\"check_execution\"}]}]}]",
          "logs": [
            {
              "msg_index": 0,
              "log": "",
              "events": [
                {
                  "type": "message",
                  "attributes": [
                    {
                      "key": "action",
                      "value": "check_execution"
                    }
                  ]
                }
              ]
            }
          ],
          "gas_wanted": "400000",
          "gas_used": "104892",
          "tx": {
            "type": "cosmos-sdk/StdTx",
            "value": {
              "msg": [
                {
                  "type": "pylons/CheckExecution",
                  "value": {
                    "ExecID": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt3376c91783d-69c2-48a4-a97b-5bd7a67a6be1",
                    "Sender": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
                    "PayToComplete": true
                  }
                }
              ],
              "fee": {
                "amount": [],
                "gas": "400000"
              },
              "signatures": [
                {
                  "pub_key": {
                    "type": "tendermint/PubKeySecp256k1",
                    "value": "A5PAksiR77+c5CrjVo4m6RTAnHW+2hKkjO9jj+GKhL/h"
                  },
                  "signature": "bYCm9/sZMPBR1jEPgCsODMTvvXSSW6n4kYj27mg039gptlKftDOUTCdwLbq9xl4zpR+N60SGAn4eY3+DbiJy6A=="
                }
              ],
              "memo": ""
            }
          },
          "timestamp": "2020-08-01T03:13:44Z"
        }
    """.trimIndent()

    private val updateRecipeResponse = """
        {
          "height": "2610",
          "txhash": "E1DA899DC546450BBDD3CBA5C5746DDF5038DF78DCEA924005C6A36DBFA76F61",
          "data": "7B225265636970654944223A22636F736D6F733179387679736739686D7661766B64787076636376327665336E7373763561766D306B7433333738623738643238312D613035652D343561382D623962632D393036396138653564373362222C224D657373616765223A227375636365737366756C6C7920757064617465642074686520726563697065222C22537461747573223A2253756363657373227D",
          "raw_log": "[{\"msg_index\":0,\"log\":\"\",\"events\":[{\"type\":\"message\",\"attributes\":[{\"key\":\"action\",\"value\":\"update_recipe\"}]}]}]",
          "logs": [
            {
              "msg_index": 0,
              "log": "",
              "events": [
                {
                  "type": "message",
                  "attributes": [
                    {
                      "key": "action",
                      "value": "update_recipe"
                    }
                  ]
                }
              ]
            }
          ],
          "gas_wanted": "400000",
          "gas_used": "47511",
          "tx": {
            "type": "cosmos-sdk/StdTx",
            "value": {
              "msg": [
                {
                  "type": "pylons/UpdateRecipe",
                  "value": {
                    "Name": "RTEST_1596250068",
                    "CookbookID": "-124015487",
                    "ID": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt3378b78d281-a05e-45a8-b9bc-9069a8e5d73b",
                    "CoinInputs": [
                      {
                        "Coin": "pylon",
                        "Count": "10"
                      }
                    ],
                    "ItemInputs": null,
                    "Entries": {
                      "CoinOutputs": null,
                      "ItemModifyOutputs": null,
                      "ItemOutputs": [
                        {
                          "ID": "itemEarth",
                          "Doubles": [
                            {
                              "Rate": "1",
                              "Key": "Mass",
                              "WeightRanges": [
                                {
                                  "Lower": "100",
                                  "Upper": "200",
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
                              "Value": "Earth",
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
                          "itemEarth"
                        ],
                        "Weight": "1"
                      }
                    ],
                    "BlockInterval": "1",
                    "Sender": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
                    "Description": "test recipe from test suite"
                  }
                }
              ],
              "fee": {
                "amount": [],
                "gas": "400000"
              },
              "signatures": [
                {
                  "pub_key": {
                    "type": "tendermint/PubKeySecp256k1",
                    "value": "A5PAksiR77+c5CrjVo4m6RTAnHW+2hKkjO9jj+GKhL/h"
                  },
                  "signature": "o3l5SIOimBZvgRYhquOhYqU9ae/TxSZTJ0aYe3yQaRJW23TSG6qD85+Didrcm3hmOOGQ9LberjpgGb3w8u+Wdw=="
                }
              ],
              "memo": ""
            }
          },
          "timestamp": "2020-08-01T02:47:45Z"
        }
    """.trimIndent()

    private val invalidDataResponse = """
        {
          "height": "2610",
          "txhash": "E1DA899DC546450BBDD3CBA5C5746DDF5038DF78DCEA924005C6A36DBFA76F61",
          "data": "",
          "raw_log": "[{\"msg_index\":0,\"log\":\"\",\"events\":[{\"type\":\"message\",\"attributes\":[{\"key\":\"action\",\"value\":\"update_recipe\"}]}]}]",
          "logs": [
            {
              "msg_index": 0,
              "log": "",
              "events": [
                {
                  "type": "message",
                  "attributes": [
                    {
                      "key": "action",
                      "value": "update_recipe"
                    }
                  ]
                }
              ]
            }
          ],
          "gas_wanted": "400000",
          "gas_used": "47511",
          "tx": {
            "type": "cosmos-sdk/StdTx",
            "value": {
              "msg": [
                {
                  "type": "pylons/UpdateRecipe",
                  "value": {
                    "Name": "RTEST_1596250068",
                    "CookbookID": "-124015487",
                    "ID": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt3378b78d281-a05e-45a8-b9bc-9069a8e5d73b",
                    "CoinInputs": [
                      {
                        "Coin": "pylon",
                        "Count": "10"
                      }
                    ],
                    "ItemInputs": null,
                    "Entries": {
                      "CoinOutputs": null,
                      "ItemModifyOutputs": null,
                      "ItemOutputs": [
                        {
                          "ID": "itemEarth",
                          "Doubles": [
                            {
                              "Rate": "1",
                              "Key": "Mass",
                              "WeightRanges": [
                                {
                                  "Lower": "100",
                                  "Upper": "200",
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
                              "Value": "Earth",
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
                          "itemEarth"
                        ],
                        "Weight": "1"
                      }
                    ],
                    "BlockInterval": "1",
                    "Sender": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
                    "Description": "test recipe from test suite"
                  }
                }
              ],
              "fee": {
                "amount": [],
                "gas": "400000"
              },
              "signatures": [
                {
                  "pub_key": {
                    "type": "tendermint/PubKeySecp256k1",
                    "value": "A5PAksiR77+c5CrjVo4m6RTAnHW+2hKkjO9jj+GKhL/h"
                  },
                  "signature": "o3l5SIOimBZvgRYhquOhYqU9ae/TxSZTJ0aYe3yQaRJW23TSG6qD85+Didrcm3hmOOGQ9LberjpgGb3w8u+Wdw=="
                }
              ],
              "memo": ""
            }
          },
          "timestamp": "2020-08-01T02:47:45Z"
        }
    """.trimIndent()

    @Test
    fun getTransactionResponseWithData() {
        val expected = TxData("successfully executed the recipe", "Success", listOf(
                TxDataOutput("COIN", "loudcoin", 2, ""),
                TxDataOutput("ITEM", "", 0, "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c0617c9e0-07ad-4052-96a0-d143c4ce015f")
        ))
        val dataString = ProtoJsonUtil.TxProtoResponseParser(executeRecipeResponse)
        val tx = Transaction.parseTransactionResponse("706D5C01916B0243CE44C99BE2C27CEC38B9BF60BBEA9B17C7EFF7A4370DE9A0", executeRecipeResponse, dataString)
        Assertions.assertEquals(expected, tx.txData)
    }

    @Test
    fun getTransactionErrorResponse() {
        val expected = TxData("", "", listOf())
        val dataString = ProtoJsonUtil.TxProtoResponseParser(errorResponse)
        val tx = Transaction.parseTransactionResponse("BFF5DE310A37F0B3A0E550A40074478824CC00D8760EAF28B378DAF91E6BCA03", errorResponse, dataString)
        Assertions.assertEquals(expected, tx.txData)

        val expectedTransaction = Transaction(
                txData = expected,
                _id = "BFF5DE310A37F0B3A0E550A40074478824CC00D8760EAF28B378DAF91E6BCA03",
                stdTx = StdTx(msg = listOf(ExecuteRecipe("LOUD-get-character-recipe-v0.1.0-1589853708", "cosmos1cmdcfat6n8vhlysnlzyqsnlty2wrkx05uyp7ez", listOf())),
                        fee = StdFee(listOf(), 400000L),
                        signatures = listOf(StdSignature(signature = "WzdzUlobvGaTkr6Y7SdaqWelmY12ERsWeNwKhzpAtnwBgz+aIhV0hMJsV61MdqpwlAz1epY5QZvL306By87dLQ==", pubKey = PubKey(type = "tendermint/PubKeySecp256k1", value = "AiRKdkdNgsMV6k21jFD1Wswyow0raUpx/gC+jE5v1STP"))), memo = ""),
                code = Transaction.ResponseCode.of(18),
                raw_log = "invalid request: The recipe doesn't exist: failed to execute message; message index: 0"
        )
        Assertions.assertEquals(expectedTransaction, tx)
    }

    @Test
    fun getTransactionCheckExecutionResponse() {
        val expected = TxData("successfully completed the execution", "Success", listOf(
                TxDataOutput("ITEM", "", 0, "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337c07589a5-eeda-47d7-a80e-bf61652d025c")
        ))
        val dataString = ProtoJsonUtil.TxProtoResponseParser(checkExecutionResponse)
        val tx = Transaction.parseTransactionResponse("A85DCD7536C991657DD7D352760A6D0C18A8A7C634B33C7EDCC8CB2C9324E40C", checkExecutionResponse, dataString)
        Assertions.assertEquals(expected, tx.txData)
    }

    /*
      has recipeId field only
      not using at right now
    */
    @Test
    fun getTransactionUpdateRecipeResponse() {
        val expected = TxData("successfully updated the recipe", "Success", listOf())
        val dataString = ProtoJsonUtil.TxProtoResponseParser(updateRecipeResponse)
        val tx = Transaction.parseTransactionResponse("E1DA899DC546450BBDD3CBA5C5746DDF5038DF78DCEA924005C6A36DBFA76F61", updateRecipeResponse, dataString)
        Assertions.assertEquals(expected, tx.txData)
    }

    /*
        data is not valid hex string
        should not come from api, but handling it
    */
    @Test
    fun getTransactionInvalidData() {
        val expected = TxData("", "", listOf())
        val dataString = ProtoJsonUtil.TxProtoResponseParser(invalidDataResponse)
        val tx = Transaction.parseTransactionResponse("E1DA899DC546450BBDD3CBA5C5746DDF5038DF78DCEA924005C6A36DBFA76F61", invalidDataResponse, dataString)
        Assertions.assertEquals(expected, tx.txData)
    }
}