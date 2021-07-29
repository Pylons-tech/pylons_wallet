package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import tech.pylons.wallet.core.Core

abstract class GetRecipeTask : DefaultTask() {
    @TaskAction
    fun getRecipe () {
        if (project.hasProperty("filepath")) {
            val fp = project.property("filepath") as String
            val recipe = RecipeManagementPlugin.loadedRecipes[fp]!!
            Core.current!!.getRecipes().forEach {
                if (it.cookbookId == recipe.cookbook && it.name == recipe.name) {
                    recipe.addRemoteState(it)
                }
            }
        }
        else {
            RecipeManagementPlugin.loadedCookbooks.values.forEach {
                val remote = Core.current!!.getRecipesByCookbook(it.id)
                remote.forEach {
                    if (RecipeManagementPlugin.loadedRecipes["${it.cookbookId}/${it.name}"] != null)
                        RecipeManagementPlugin.loadedRecipes["${it.cookbookId}/${it.name}"]!!.addRemoteState(it)
                    else {
                        RecipeManagementPlugin.loadedRecipes["${it.cookbookId}/${it.name}"] = MetaRecipe(it.name, it.cookbookId,
                            MetaRecipe.version(it),
                            mutableMapOf(RecipeManagementPlugin.currentRemote.identifier() to it.id),
                            mutableMapOf(MetaRecipe.version(it) to it),
                            mutableMapOf(RecipeManagementPlugin.currentRemote.identifier() to it),
                            mutableMapOf(RecipeManagementPlugin.currentRemote.identifier() to MetaRecipe.version(it)),
                            mutableMapOf(RecipeManagementPlugin.currentRemote.identifier() to it.disabled)
                        )
                    }
                }
                RecipeManagementPlugin.loadedRecipes.values.forEach { meta ->
                    remote.forEach {
                        if (it.cookbookId == meta.cookbook && it.name == meta.name)
                            meta.addRemoteState(it)
                    }
                }
            }
        }
    }
}