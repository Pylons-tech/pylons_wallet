package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import tech.pylons.lib.klaxon
import tech.pylons.wallet.core.Core

abstract class CreateRecipeTask : DefaultTask() {
    @TaskAction
    fun createRecipe () {
        val recipe = RecipeManagementPlugin.getRecipeFromPath(project.property("filepath") as String)
        Core.current!!.getProfile()
        Core.current!!.batchCreateRecipe(
            names = listOf(recipe.name),
            cookbooks = listOf(recipe.cookbookId),
            descriptions = listOf(recipe.description),
            blockIntervals = listOf(recipe.blockInterval),
            coinInputs = listOf(klaxon.toJsonString(recipe.coinInputs)),
            itemInputs = listOf(klaxon.toJsonString(recipe.itemInputs)),
            outputTables = listOf(klaxon.toJsonString(recipe.entries)),
            outputs = listOf(klaxon.toJsonString(recipe.outputs))
        )
    }
}