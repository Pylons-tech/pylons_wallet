package com.pylons.wallet.core.types.jsonTemplate

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.PylonsSECP256K1 as PylonsSECP256K1
import java.lang.StringBuilder

internal fun executeRecipe (recipeId : String, itemIds : Array<String>, sender: String, pubkey: PylonsSECP256K1.PublicKey, accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(Core.statusBlock.height, executeRecipeMsgTemplate(recipeId, itemIds, sender),
                executeRecipeSignTemplate( recipeId, itemIds, sender),
                accountNumber, sequence, pubkey)

private fun executeRecipeMsgTemplate (id : String, itemIds : Array<String>, sender : String) = """
        [
        {
            "type": "pylons/ExecuteRecipe",
            "value": {
                "ItemIDs":${itemIdListJson(itemIds)},
                "RecipeID":"$id",
                "Sender": "$sender"
            }
        }
        ]     
    """

fun executeRecipeSignTemplate (id : String, itemIds : Array<String>, sender: String) : String =
        """[{"ItemIDs":${itemIdListJson(itemIds)},"RecipeID":"$id","Sender":"$sender"}]"""

private fun itemIdListJson (itemIds: Array<String>) : String {
    val sb = StringBuilder("[")
    itemIds.forEach { sb.append(""""$it",""") }
    if (sb.length > 1) sb.deleteCharAt(sb.length - 1)
    sb.append(']')
    return when (val output = sb.toString()) {
        "[]" -> "null"
        else -> output
    }
}