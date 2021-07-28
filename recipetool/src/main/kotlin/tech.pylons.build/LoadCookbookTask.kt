package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class LoadCookbookTask : DefaultTask() {
    @TaskAction
    fun loadCookbook () {
        if (project.hasProperty("filepath")) {
            val fp = project.property("filepath") as String
            try {
                RecipeManagementPlugin.loadCookbook(fp)
            } catch (e : NullPointerException) {
                // swallow for now
            }
        }
        else {
            val cbs = RecipeManagementPlugin.enumerateCookbookRecords()
            println ("Retrieved ${cbs.size} cookbook records")
            cbs.forEach {
                try {
                    RecipeManagementPlugin.loadCookbook(it)
                } catch (e : NullPointerException) {
                    // swallow for now
                }
            }
        }
    }
}