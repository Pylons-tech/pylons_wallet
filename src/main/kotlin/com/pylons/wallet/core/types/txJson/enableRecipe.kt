package com.pylons.wallet.core.types.txJson

import com.pylons.wallet.core.types.SECP256K1

internal fun enableRecipe (recipeId : String, sender: String,
                  pubkey: SECP256K1.PublicKey, accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(enableRecipeMsgTemplate(recipeId, sender),
                enableRecipeSignTemplate(recipeId, sender),
                accountNumber, sequence, pubkey)

private fun enableRecipeMsgTemplate (id : String, sender : String) = """
        [
        {
            "type": "pylons/EnableRecipe",
            "value": {
                "RecipeID":"$id",
                "Sender": "$sender"
            }
        }
        ]     
    """

internal fun enableRecipeSignTemplate (id : String, sender: String) : String =
        """[{"RecipeID":"$id","Sender":"$sender"}]"""