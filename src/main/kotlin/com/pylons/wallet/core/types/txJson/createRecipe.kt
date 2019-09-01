package com.pylons.wallet.core.types.txJson

import com.pylons.wallet.core.types.SECP256K1

internal fun createRecipe (name: String, cookbookName : String, desc : String, inputs : Map<String, Long>, outputs : Map<String, Long>,
                  time : Long, sender : String, pubkey: SECP256K1.PublicKey, accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(createRecipeMsgTemplate(name, cookbookName, desc, getInputOutputListForMessage(inputs),
                getInputOutputListForMessage(outputs), time, sender),
                createRecipeSignTemplate(name, cookbookName, desc, time,
                        getInputOutputListForSigning(inputs), getInputOutputListForSigning(outputs), sender),
                accountNumber, sequence, pubkey)

private fun createRecipeMsgTemplate (name : String, cookbookName : String, desc : String, inputs : String, outputs : String,
                                     time : Long, sender : String) = """
        [
        {
            "type": "pylons/CreateRecipe",
            "value": {
                "RecipeName": "$name",
                "CookbookName": "$cookbookName",
                "Description": "$desc",
                "CoinInputs": $inputs,
                "CoinOutputs": $outputs,
                "ExecutionTime": "$time",
                "Sender": "$sender"
            }
        }
        ]     
    """

internal fun createRecipeSignTemplate (name : String, cookbookName: String, desc: String, time: Long, inputs: String, outputs: String, sender: String) =
        """[{"CoinInputs":$inputs,"CoinOutputs":$outputs,"CookbookName":"$cookbookName","Description":"$desc","ExecutionTime":$time,"RecipeName":"$name","Sender":"$sender"}]"""
