package com.pylons.wallet.core.types.txJson

import com.pylons.wallet.core.types.SECP256K1

internal fun executeRecipe (recipeId : String, sender: String, inputs : Map<String, Long>,
                   pubkey: SECP256K1.PublicKey, accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(executeRecipeMsgTemplate(recipeId, getCoinIOListForMessage(inputs), sender),
                executeRecipeSignTemplate(getCoinIOListForSigning(inputs), recipeId, sender),
                accountNumber, sequence, pubkey)

private fun executeRecipeMsgTemplate (id : String, inputs : String, sender : String) = """
        [
        {
            "type": "pylons/ExecuteRecipe",
            "value": {
                "RecipeID":"$id",
                "CoinInputs": $inputs,
                "Sender": "$sender"
            }
        }
        ]     
    """

internal fun executeRecipeSignTemplate (inputs : String, id : String, sender: String) : String =
        """[{"CoinInputs":$inputs,"RecipeID":"$id","Sender":"$sender"}]"""