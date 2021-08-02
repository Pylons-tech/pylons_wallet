package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class SaveCookbookTask : DefaultTask() {
    @TaskAction
    fun saveCookbook () {
        if (project.hasProperty("filepath")) {
            val fp = project.property("filepath") as String
            try {
                RecipeManagementPlugin.saveCookbook(RecipeManagementPlugin.loadedCookbooks[fp]!!)
            } catch (e : NullPointerException) {
                // swallow for now
            }
        }
        else {
            val cbs = RecipeManagementPlugin.enumerateCookbookRecords()
            println ("Retrieved ${cbs.size} cookbook records")
            cbs.forEach {
                try {
                    RecipeManagementPlugin.saveCookbook(RecipeManagementPlugin.loadedCookbooks[it]!!)
                } catch (e : NullPointerException) {
                    // swallow for now
                }
            }
        }
    }
}