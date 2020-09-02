package com.pylons.wallet.core.types.jsonTemplate

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.PylonsSECP256K1

internal fun enableRecipe (recipeId : String, sender: String,
                           pubkey: PylonsSECP256K1.PublicKey, accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(Core.statusBlock.height, enableRecipeMsgTemplate(recipeId, sender),
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

fun enableRecipeSignTemplate (id : String, sender: String) : String =
        """[{"RecipeID":"$id","Sender":"$sender"}]"""