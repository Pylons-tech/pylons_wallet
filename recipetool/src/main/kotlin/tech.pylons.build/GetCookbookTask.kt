package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import tech.pylons.wallet.core.Core

abstract class GetCookbookTask : DefaultTask() {
    @TaskAction
    fun getCookbook () {
        if (project.hasProperty("filepath")) {
            val fp = project.property("filepath") as String
            val cookbook = RecipeManagementPlugin.loadedCookbooks[fp]
            Core.current!!.getCookbooks().forEach {
                if (it.id == cookbook!!.id) {
                    cookbook.addRemoteState(it)
                }
            }
        }
        else {
            val remote = Core.current!!.getCookbooks()
            remote.forEach {
                if (RecipeManagementPlugin.loadedCookbooks[it.id] != null)
                    RecipeManagementPlugin.loadedCookbooks[it.id]!!.addRemoteState(it)
                else {
                    RecipeManagementPlugin.loadedCookbooks[it.id] = MetaCookbook(it.id, it.version,
                        mutableMapOf(it.version to it),
                        mutableMapOf(RecipeManagementPlugin.currentRemote.identifier() to it)
                    )
                }
            }
            RecipeManagementPlugin.loadedCookbooks.values.forEach { meta ->
                remote.forEach {
                    if (it.id == meta.id)
                        meta.addRemoteState(it)
                }
            }
        }
    }
}