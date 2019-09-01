package com.pylons.wallet.core.types.txJson

import com.pylons.wallet.core.types.SECP256K1

internal fun disableRecipe (recipeId : String, sender: String,
                   pubkey: SECP256K1.PublicKey, accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(disableRecipeMsgTemplate(recipeId, sender),
                disableRecipeSignTemplate(recipeId, sender),
                accountNumber, sequence, pubkey)

private fun disableRecipeMsgTemplate (id : String, sender : String) = """
        [
        {
            "type": "pylons/DisableRecipe",
            "value": {
                "RecipeID":"$id",
                "Sender": "$sender"
            }
        }
        ]     
    """

internal fun disableRecipeSignTemplate (id : String, sender: String) : String =
        enableRecipeSignTemplate(id, sender)