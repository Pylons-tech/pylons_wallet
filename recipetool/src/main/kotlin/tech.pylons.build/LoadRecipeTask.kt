package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class LoadRecipeTask : DefaultTask() {
    @TaskAction
    fun loadRecipe () {
        if (project.hasProperty("filepath")) {
            val fp = project.property("filepath") as String
            try {
                RecipeManagementPlugin.loadRecipe(fp)
            } catch (e : NullPointerException) {
                // swallow for now
            }
        }
        else {
            val rs = RecipeManagementPlugin.enumerateRecipeRecords()
            println ("Retrieved ${rs.size} recipe records")
            rs.forEach {
                try {
                    RecipeManagementPlugin.loadRecipe(it)
                } catch (e : NullPointerException) {
                    // swallow for now
                }
            }
        }
    }
}