package walletcore.ops

import walletcore.Core
import walletcore.constants.Keys
import walletcore.internal.generateErrorMessageData
import walletcore.types.*

internal fun applyRecipe (msg: MessageData) : Response {
    Core.foreignProfilesBuffer = setOf()
    if (!msg.strings.containsKey(Keys.cookbook)) return generateErrorMessageData(Error.MALFORMED_TX, "Did not provide an ID for required cookbook.")
    if (!msg.strings.containsKey(Keys.recipe)) return generateErrorMessageData(Error.MALFORMED_TX, "Did not provide an ID for required recipe.")
    val preferredItemIds = msg.stringArrays[Keys.preferredItemIds] ?: mutableListOf()
    val cookbook = when (Core.loadedCookbooks.containsKey(msg.strings[Keys.cookbook])) {
        true -> Core.loadedCookbooks[msg.strings[Keys.cookbook]]
        false -> Core.txHandler.loadCookbook(msg.strings[Keys.cookbook]!!)
    } ?: return generateErrorMessageData(Error.COOKBOOK_DOES_NOT_EXIST, "Specified cookbook could not be loaded.")
    val recipe = cookbook.recipes[msg.strings[Keys.recipe]] ?: return generateErrorMessageData(Error.RECIPE_DOES_NOT_EXIST, "Recipe not in cookbook.")
    val prf = Core.txHandler.applyRecipe(cookbook, recipe, preferredItemIds.toSet())
    val msg = when (prf) {
        null -> MessageData(booleans = mutableMapOf(Keys.success to false))
        else -> MessageData(booleans = mutableMapOf(Keys.success to true))
    }
    return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
}