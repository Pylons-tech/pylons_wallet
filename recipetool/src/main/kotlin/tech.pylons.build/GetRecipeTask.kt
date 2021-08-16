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
                remote.forEach { rcp ->
                    if (RecipeManagementPlugin.loadedRecipes["${rcp.cookbookId}/${rcp.name}"] != null)
                        RecipeManagementPlugin.loadedRecipes["${rcp.cookbookId}/${rcp.name}"]!!.addRemoteState(rcp)
                    else {
                        RecipeManagementPlugin.loadedRecipes["${rcp.cookbookId}/${rcp.name}"] = MetaRecipe(rcp.name, rcp.cookbookId,
                            MetaRecipe.version(rcp),
                            mutableMapOf(RecipeManagementPlugin.currentRemote.identifier() to rcp.id),
                            mutableMapOf(MetaRecipe.version(rcp) to rcp),
                            mutableMapOf(RecipeManagementPlugin.currentRemote.identifier() to rcp),
                            mutableMapOf(RecipeManagementPlugin.currentRemote.identifier() to MetaRecipe.version(rcp)),
                            mutableMapOf(RecipeManagementPlugin.currentRemote.identifier() to rcp.disabled)
                        )
                    }
                }
                RecipeManagementPlugin.loadedRecipes.values.forEach { meta ->
                    remote.forEach { rcp ->
                        if (rcp.cookbookId == meta.cookbook && rcp.name == meta.name)
                            meta.addRemoteState(rcp)
                    }
                }
            }
        }
    }
}