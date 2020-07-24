package com.pylons.wallet.walletcore_test.types.transaction

import com.pylons.wallet.core.types.Transaction
import com.pylons.wallet.core.types.tx.*
import com.pylons.wallet.core.types.tx.msg.ExecuteRecipe
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
          "height": "25213",
          "txhash": "F7E835A287BB9E7CBAACDD9A6D211DC1918D329DA682A93F8C0C71F236CFFFB7",
          "data": "7B224D657373616765223A22657865637574696F6E20616C726561647920636F6D706C65746564222C22537461747573223A22436F6D706C65746564222C224F7574707574223A6E756C6C7D",
          "raw_log": "[{\"msg_index\":\"0\",\"success\":true,\"log\":\"\"}]",
          "logs": [
            {
              "msg_index": "0",
              "success": true,
              "log": ""
            }
          ],
          "gas_wanted": "200000",
          "gas_used": "12314",
          "tags": [
            {
              "key": "action",
              "value": "check_execution"
            }
          ],
          "tx": {
            "type": "auth/StdTx",
            "value": {
              "msg": [
                {
                  "type": "pylons/CheckExecution",
                  "value": {
                    "ExecID": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt3372a36a8f1-6dab-4c8a-97d0-9035aa5c81ac",
                    "Sender": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
                    "PayToComplete": true
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
                    "value": "A5PAksiR77+c5CrjVo4m6RTAnHW+2hKkjO9jj+GKhL/h"
                  },
                  "signature": "EQwgo3iu740Nuwoh1N8K2ernOvfx3w+jgfCKhYKzausyUOyfJNo6qM5ZWcoMfIgbHVZh7NIPnPSj18qqqLX7QQ=="
                }
              ],
              "memo": ""
            }
          },
          "timestamp": "2020-05-17T21:57:37Z"
        }
    """.trimIndent()

    private val updateRecipeResponse = """
        {
          "height": "25158",
          "txhash": "A0B4A3F09E936BAF0E9F9E0085BAD76FFA0CBCF5E471E32F2C0F26876A98AD10",
          "data": "7B225265636970654944223A22636F736D6F733179387679736739686D7661766B64787076636376327665336E7373763561766D306B7433333734323962633965342D323533642D343563612D393038662D643262343534616562623337227D",
          "raw_log": "[{\"msg_index\":\"0\",\"success\":true,\"log\":\"\"}]",
          "logs": [
            {
              "msg_index": "0",
              "success": true,
              "log": ""
            }
          ],
          "gas_wanted": "200000",
          "gas_used": "38237",
          "tags": [
            {
              "key": "action",
              "value": "update_recipe"
            }
          ],
          "tx": {
            "type": "auth/StdTx",
            "value": {
              "msg": [
                {
                  "type": "pylons/UpdateRecipe",
                  "value": {
                    "Name": "RTEST_1589752383",
                    "CookbookID": "-1660484269",
                    "ID": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337429bc9e4-253d-45ca-908f-d2b454aebb37",
                    "CoinInputs": [
                      {
                        "Coin": "pylon",
                        "Count": "10"
                      }
                    ],
                    "ItemInputs": null,
                    "Entries": {
                      "CoinOutputs": null,
                      "ItemOutputs": [
                        {
                          "ModifyItem": {
                            "Doubles": null,
                            "Longs": null,
                            "Strings": null,
                            "TransferFee": 0
                          },
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
                        "ResultEntries": [
                          "0"
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
                "amount": null,
                "gas": "200000"
              },
              "signatures": [
                {
                  "pub_key": {
                    "type": "tendermint/PubKeySecp256k1",
                    "value": "A5PAksiR77+c5CrjVo4m6RTAnHW+2hKkjO9jj+GKhL/h"
                  },
                  "signature": "4Yh5dJB6tQdiJIfoXaLc6biXpat+fPFyz+nEuB86XwVRmLkVhg4ibmjGUsImgYXEI06jWhbeXcpgemEvSsADaw=="
                }
              ],
              "memo": ""
            }
          },
          "timestamp": "2020-05-17T21:53:00Z"
        }
    """.trimIndent()

    private val invalidDataResponse = """
        {
          "height": "25158",
          "txhash": "A0B4A3F09E936BAF0E9F9E0085BAD76FFA0CBCF5E471E32F2C0F26876A98AD10",
          "data": "",
          "raw_log": "[{\"msg_index\":\"0\",\"success\":true,\"log\":\"\"}]",
          "logs": [
            {
              "msg_index": "0",
              "success": true,
              "log": ""
            }
          ],
          "gas_wanted": "200000",
          "gas_used": "38237",
          "tags": [
            {
              "key": "action",
              "value": "update_recipe"
            }
          ],
          "tx": {
            "type": "auth/StdTx",
            "value": {
              "msg": [
                {
                  "type": "pylons/UpdateRecipe",
                  "value": {
                    "Name": "RTEST_1589752383",
                    "CookbookID": "-1660484269",
                    "ID": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337429bc9e4-253d-45ca-908f-d2b454aebb37",
                    "CoinInputs": [
                      {
                        "Coin": "pylon",
                        "Count": "10"
                      }
                    ],
                    "ItemInputs": null,
                    "Entries": {
                      "CoinOutputs": null,
                      "ItemOutputs": [
                        {
                          "ModifyItem": {
                            "Doubles": null,
                            "Longs": null,
                            "Strings": null,
                            "TransferFee": 0
                          },
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
                        "ResultEntries": [
                          "0"
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
                "amount": null,
                "gas": "200000"
              },
              "signatures": [
                {
                  "pub_key": {
                    "type": "tendermint/PubKeySecp256k1",
                    "value": "A5PAksiR77+c5CrjVo4m6RTAnHW+2hKkjO9jj+GKhL/h"
                  },
                  "signature": "4Yh5dJB6tQdiJIfoXaLc6biXpat+fPFyz+nEuB86XwVRmLkVhg4ibmjGUsImgYXEI06jWhbeXcpgemEvSsADaw=="
                }
              ],
              "memo": ""
            }
          },
          "timestamp": "2020-05-17T21:53:00Z"
        }
    """.trimIndent()

    @Test
    fun getTransactionResponseWithData() {
        val expected = TxData("successfully executed the recipe", "Success", listOf(
                TxDataOutput("COIN", "loudcoin", 2, ""),
                TxDataOutput("ITEM", "", 0, "cosmos1sx8wmlcm7l7rulg7fam56ngxge4fsvxq76q28c0617c9e0-07ad-4052-96a0-d143c4ce015f")
        ))
        val tx = Transaction.parseTransactionResponse("706D5C01916B0243CE44C99BE2C27CEC38B9BF60BBEA9B17C7EFF7A4370DE9A0", executeRecipeResponse)
        Assertions.assertEquals(expected, tx.txData)
    }

    @Test
    fun getTransactionErrorResponse() {
        val expected = TxData("", "", listOf())
        val tx = Transaction.parseTransactionResponse("BFF5DE310A37F0B3A0E550A40074478824CC00D8760EAF28B378DAF91E6BCA03", errorResponse)
        Assertions.assertEquals(expected, tx.txData)

        val expectedTransaction = Transaction(
                txData = expected,
                _id = "BFF5DE310A37F0B3A0E550A40074478824CC00D8760EAF28B378DAF91E6BCA03",
                stdTx = StdTx(msg = listOf(ExecuteRecipe("LOUD-get-character-recipe-v0.1.0-1589853708", "cosmos1cmdcfat6n8vhlysnlzyqsnlty2wrkx05uyp7ez", null)),
                        fee = StdFee(listOf(), 400000L),
                        signatures = listOf(StdSignature(signature = "WzdzUlobvGaTkr6Y7SdaqWelmY12ERsWeNwKhzpAtnwBgz+aIhV0hMJsV61MdqpwlAz1epY5QZvL306By87dLQ==", pubKey = PubKey(type = "tendermint/PubKeySecp256k1", value = "AiRKdkdNgsMV6k21jFD1Wswyow0raUpx/gC+jE5v1STP"))), memo = ""),
                code = 18,
                raw_log = "invalid request: The recipe doesn't exist: failed to execute message; message index: 0"
        )
        Assertions.assertEquals(expectedTransaction, tx)
    }

    @Test
    fun getTransactionCheckExecutionResponse() {
        val expected = TxData("execution already completed", "Completed", listOf())
        val tx = Transaction.parseTransactionResponse("F7E835A287BB9E7CBAACDD9A6D211DC1918D329DA682A93F8C0C71F236CFFFB7", checkExecutionResponse)
        Assertions.assertEquals(expected, tx.txData)
    }

    /*
      has recipeId field only
      not using at right now
    */
    @Test
    fun getTransactionUpdateRecipeResponse() {
        val expected = TxData("", "", listOf())
        val tx = Transaction.parseTransactionResponse("A0B4A3F09E936BAF0E9F9E0085BAD76FFA0CBCF5E471E32F2C0F26876A98AD10", updateRecipeResponse)
        Assertions.assertEquals(expected, tx.txData)
    }

    /*
        data is not valid hex string
        should not come from api, but handling it
    */
    @Test
    fun getTransactionInvalidData() {
        val expected = TxData("", "", listOf())
        val tx = Transaction.parseTransactionResponse("A0B4A3F09E936BAF0E9F9E0085BAD76FFA0CBCF5E471E32F2C0F26876A98AD10", invalidDataResponse)
        Assertions.assertEquals(expected, tx.txData)
    }
}