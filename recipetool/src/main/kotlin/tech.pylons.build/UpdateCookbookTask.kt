package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import tech.pylons.wallet.core.Core

abstract class UpdateCookbookTask : DefaultTask() {
    @TaskAction
    fun updateCookbook () {
        val path = project.property("filepath") as String
        val meta = RecipeManagementPlugin.loadCookbook(path)
        val cookbook = meta.versions[meta.targetVersions[RecipeManagementPlugin.currentRemote.identifier()]]!!
        Core.current!!.getProfile()
        Core.current!!.batchUpdateCookbook(
            ids = listOf(cookbook.id),
            names = listOf(cookbook.name),
            developers = listOf(cookbook.developer),
            descriptions = listOf(cookbook.description),
            versions = listOf(cookbook.version),
            supportEmails = listOf(cookbook.supportEmail)
        )
    }
}