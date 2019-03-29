package walletcore.ops

import walletcore.Core
import walletcore.Logger
import walletcore.constants.Keys
import walletcore.constants.LogTag
import walletcore.internal.*
import walletcore.tx.TxHandler
import walletcore.types.*

internal fun applyRecipe (msg: MessageData) : Response {
    Logger.implementation.log(msg.strings[Keys.recipe].orEmpty(), LogTag.info)
    Logger.implementation.log(msg.strings[Keys.cookbook].orEmpty(), LogTag.info)
    var error : Response? = null
    if (!isValid(msg){error = it}) return error!!
    val preferredItemIds = msg.stringArrays[Keys.preferredItemIds] ?: mutableListOf()
    val cookbook = when (Core.loadedCookbooks.containsKey(msg.strings[Keys.cookbook])) {
        true -> Core.loadedCookbooks[msg.strings[Keys.cookbook]]
        false -> Core.txHandler.loadCookbook(msg.strings[Keys.cookbook]!!)
    } ?: return generateErrorMessageData(Error.COOKBOOK_DOES_NOT_EXIST, "Specified cookbook could not be loaded.")
    val recipe = cookbook.recipes[msg.strings[Keys.recipe]] ?: return generateErrorMessageData(Error.RECIPE_DOES_NOT_EXIST, "Recipe not in cookbook.")
    if (!canApplyRecipe(recipe){error = it}) return error!!
    val output = Core.txHandler.applyRecipe(cookbook, recipe, preferredItemIds.toSet())
    val msg = when (output?.profile) {
        null -> MessageData(booleans = mutableMapOf(Keys.success to false))
        else -> MessageData(booleans = mutableMapOf(Keys.success to true))
    }
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}

private fun canApplyRecipe (recipe: Recipe, handleError: (Response) -> Unit) : Boolean {
    val profile = Core.userProfile!!
    if (!profile.canPayCoins(recipe.coinsIn)) { handleError(generateFailureMessageData(
            "Local user profile doesn't have coins for recipe.")); return false }
    if (!profile.canPayCoins(recipe.coinCatalysts)) { handleError(generateFailureMessageData(
                "Local user profile doesn't have coins for recipe catalysts.")); return false }
    if (!profile.canPayItems(recipe.itemsIn)) { handleError(generateFailureMessageData(
            "Local user profile doesn't have items for recipe.")); return false }
    if (!profile.canPayItems(recipe.itemCatalysts)) { handleError(generateFailureMessageData(
            "Local user profile doesn't have items for recipe catalysts.")); return false }
    return true
}

private fun isValid (msg : MessageData, handleError : (Response) -> Unit) : Boolean {
    System.out.println("A")
    if (!msg.strings.containsKey(Keys.cookbook)) { handleError(generateErrorMessageData(Error.MALFORMED_TX,
            "Did not provide an ID for required cookbook.")); return false }
    System.out.println("B")
    if (!msg.strings.containsKey(Keys.recipe)) { handleError(generateErrorMessageData(Error.MALFORMED_TX,
            "Did not provide an ID for required recipe.")); return false }
    return true
}