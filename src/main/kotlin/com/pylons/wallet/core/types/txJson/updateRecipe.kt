package com.pylons.wallet.core.types.txJson

import com.pylons.wallet.core.types.SECP256K1

internal fun updateRecipe (name: String, cookbookName : String, id: String, desc : String, inputs : Map<String, Long>, outputs : Map<String, Long>,
                  time : Long, sender : String, pubkey: SECP256K1.PublicKey, accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(updateRecipeMsgTemplate(name, cookbookName, id, desc, getInputOutputListForMessage(inputs),
                getInputOutputListForMessage(outputs), time, sender),
                updateRecipeSignTemplate(name, cookbookName, id, desc, time,
                        getInputOutputListForSigning(inputs), getInputOutputListForSigning(outputs), sender),
                accountNumber, sequence, pubkey)

private fun updateRecipeMsgTemplate (name : String, cookbookName : String, id : String, desc : String, inputs : String, outputs : String,
                                     time : Long, sender : String) = """
        [
        {
            "type": "pylons/UpdateRecipe",
            "value": {
                "RecipeName": "$name",
                "ID":"$id",
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

internal fun updateRecipeSignTemplate (name : String, cookbookName: String, id : String, desc: String, time: Long, inputs: String, outputs: String, sender: String) =
        """[{"CoinInputs":$inputs,"CoinOutputs":$outputs,"CookbookName":"$cookbookName","Description":"$desc","ExecutionTime":$time,"ID":"$id","RecipeName":"$name","Sender":"$sender"}]"""

