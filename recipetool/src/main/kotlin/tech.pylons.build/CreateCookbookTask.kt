package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import tech.pylons.wallet.core.Core

abstract class CreateCookbookTask : DefaultTask() {
    @TaskAction
    fun createCookbook () {
        val cookbook = RecipeManagementPlugin.getCookbookFromPath(project.property("filepath") as String)
        Core.current!!.batchCreateCookbook(
            ids = listOf(cookbook.id),
            names = listOf(cookbook.name),
            developers = listOf(cookbook.developer),
            descriptions = listOf(cookbook.description),
            versions = listOf(cookbook.version),
            supportEmails = listOf(cookbook.supportEmail),
            levels = listOf(cookbook.level),
            costsPerBlock = listOf(cookbook.costPerBlock)
        )
    }
}