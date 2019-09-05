package com.pylons.wallet.core.types.txJson

import com.pylons.wallet.core.types.SECP256K1

internal fun executeRecipe (recipeId : String, sender: String, pubkey: SECP256K1.PublicKey, accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(executeRecipeMsgTemplate(recipeId, sender),
                executeRecipeSignTemplate( recipeId, sender),
                accountNumber, sequence, pubkey)

private fun executeRecipeMsgTemplate (id : String, sender : String) = """
        [
        {
            "type": "pylons/ExecuteRecipe",
            "value": {
                "RecipeID":"$id",
                "Sender": "$sender"
            }
        }
        ]     
    """

internal fun executeRecipeSignTemplate (id : String, sender: String) : String =
        """[{"RecipeID":"$id","Sender":"$sender"}]"""