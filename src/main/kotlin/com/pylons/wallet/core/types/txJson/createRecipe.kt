package com.pylons.wallet.core.types.txJson

import com.pylons.wallet.core.types.Item
import com.pylons.wallet.core.types.SECP256K1

internal fun createRecipe (name: String, cookbookName : String, desc : String, coinInputs : Map<String, Long>, coinOutputs : Map<String, Long>,
                           itemInputs: Array<Item>, itemOutputs : Array<Item>, time : Long, sender : String, pubkey: SECP256K1.PublicKey,
                           accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(createRecipeMsgTemplate(name, cookbookName, desc, getCoinIOListForMessage(coinInputs),
                getCoinIOListForMessage(coinOutputs), time, sender),
                createRecipeSignTemplate(name, cookbookName, desc, time,
                        getCoinIOListForSigning(coinInputs), getCoinIOListForSigning(coinOutputs), sender),
                accountNumber, sequence, pubkey)

private fun createRecipeMsgTemplate (name : String, cookbookName : String, desc : String, coinInputs : String, coinOutputs : String,
                                     itemInputs : String, itemOutputs: String, time : Long, sender : String) = """
        [
        {
            "type": "pylons/CreateRecipe",
            "value": {
                "RecipeName": "$name",
                "CookbookName": "$cookbookName",
                "Description": "$desc",
                "CoinInputs": $coinInputs,
                "CoinOutputs": $coinOutputs,
                "ItemInputs": $itemInputs,
                "ItemOutputs": $itemOutputs,
                "ExecutionTime": "$time",
                "Sender": "$sender"
            }
        }
        ]     
    """

internal fun createRecipeSignTemplate (name : String, cookbookName: String, desc: String, time: Long, inputs: String, outputs: String, sender: String) =
        """[{"CoinInputs":$inputs,"CoinOutputs":$outputs,"CookbookName":"$cookbookName","Description":"$desc","ExecutionTime":$time,"RecipeName":"$name","Sender":"$sender"}]"""
