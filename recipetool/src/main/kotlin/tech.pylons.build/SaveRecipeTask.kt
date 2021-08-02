package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class SaveRecipeTask : DefaultTask() {
    @TaskAction
    fun saveRecipe () {
        if (project.hasProperty("filepath")) {
            val fp = project.property("filepath") as String
            try {
                RecipeManagementPlugin.saveRecipe(RecipeManagementPlugin.loadedRecipes[fp]!!)
            } catch (e : NullPointerException) {
                // swallow for now
            }
        }
        else {
            val cbs = RecipeManagementPlugin.enumerateCookbookRecords()
            println ("Retrieved ${cbs.size} cookbook records")
            cbs.forEach {
                try {
                    RecipeManagementPlugin.saveRecipe(RecipeManagementPlugin.loadedRecipes[it]!!)
                } catch (e : NullPointerException) {
                    // swallow for now
                }
            }
        }
    }
}