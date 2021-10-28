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
            creators = listOf(recipe.id),
            cookbooks = listOf(recipe.cookbookId),
            ids = listOf(recipe.id),
            names = listOf(recipe.name),
            descriptions = listOf(recipe.description),
            versions = listOf(recipe.version),
            coinInputs = listOf(recipe.coinInputs),
            itemInputs = listOf(recipe.itemInputs),
            outputTables = listOf(recipe.entries),
            outputs = listOf(recipe.outputs),
            blockIntervals = listOf(recipe.blockInterval),
            enableds = listOf(recipe.enabled),
            extraInfos = listOf()
        )
    }
}