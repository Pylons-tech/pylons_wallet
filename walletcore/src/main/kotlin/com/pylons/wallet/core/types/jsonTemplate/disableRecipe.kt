package com.pylons.wallet.core.types.jsonTemplate

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.PylonsSECP256K1

internal fun disableRecipe (recipeId : String, sender: String,
                            pubkey: PylonsSECP256K1.PublicKey, accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(Core.statusBlock.height, disableRecipeMsgTemplate(recipeId, sender),
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

fun disableRecipeSignTemplate (id : String, sender: String) : String =
        enableRecipeSignTemplate(id, sender)