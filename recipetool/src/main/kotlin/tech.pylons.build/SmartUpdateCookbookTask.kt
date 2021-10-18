package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.targets.js.npm.SemVer
import tech.pylons.wallet.core.Core

abstract class SmartUpdateCookbookTask : DefaultTask()  {
    @TaskAction
    fun smartUpdateCookbook () {
        val identifier = RecipeManagementPlugin.currentRemote.identifier()
        if (project.hasProperty("filepath")) {
            Core.current!!.getProfile()
            val meta = RecipeManagementPlugin.loadedCookbooks[project.property("filepath") as String]!!
            if (SemVer.from(meta.remotes[identifier]!!.version) <
                SemVer.from(meta.targetVersions[identifier]!!))
                Core.current!!.batchUpdateCookbook(
                    creators = listOf(meta.versions[meta.targetVersions[identifier]]!!.Creator),
                    ids = listOf(meta.versions[meta.targetVersions[identifier]]!!.id),
                    names = listOf(meta.versions[meta.targetVersions[identifier]]!!.name),
                    descriptions = listOf(meta.versions[meta.targetVersions[identifier]]!!.description),
                    developers = listOf(meta.versions[meta.targetVersions[identifier]]!!.developer),
                    versions = listOf(meta.versions[meta.targetVersions[identifier]]!!.version),
                    supportEmails = listOf(meta.versions[meta.targetVersions[identifier]]!!.supportEmail),
                    costPerBlocks = listOf(meta.versions[meta.targetVersions[identifier]]!!.costPerBlock),
                    enableds = listOf(meta.versions[meta.targetVersions[identifier]]!!.Enabled)
                )
        }
        else {
            RecipeManagementPlugin.loadedCookbooks.values.forEach { meta ->
                Core.current!!.getProfile()
                if (meta.remotes.containsKey(identifier) && SemVer.from(meta.remotes[identifier]!!.version) <
                    SemVer.from(meta.targetVersions[identifier]!!)) {
                    Core.current!!.batchUpdateCookbook(
                        creators = listOf(meta.versions[meta.targetVersions[identifier]]!!.Creator),
                        ids = listOf(meta.versions[meta.targetVersions[identifier]]!!.id),
                        names = listOf(meta.versions[meta.targetVersions[identifier]]!!.name),
                        descriptions = listOf(meta.versions[meta.targetVersions[identifier]]!!.description),
                        developers = listOf(meta.versions[meta.targetVersions[identifier]]!!.developer),
                        versions = listOf(meta.versions[meta.targetVersions[identifier]]!!.version),
                        supportEmails = listOf(meta.versions[meta.targetVersions[identifier]]!!.supportEmail),
                        costPerBlocks = listOf(meta.versions[meta.targetVersions[identifier]]!!.costPerBlock),
                        enableds = listOf(meta.versions[meta.targetVersions[identifier]]!!.Enabled)
                    )
                }
                else Core.current!!.batchCreateCookbook(
                    creators = listOf(meta.versions[meta.targetVersions[identifier]]!!.Creator),
                    ids = listOf(meta.versions[meta.targetVersions[identifier]]!!.id),
                    names = listOf(meta.versions[meta.targetVersions[identifier]]!!.name),
                    descriptions = listOf(meta.versions[meta.targetVersions[identifier]]!!.description),
                    developers = listOf(meta.versions[meta.targetVersions[identifier]]!!.developer),
                    versions = listOf(meta.versions[meta.targetVersions[identifier]]!!.version),
                    supportEmails = listOf(meta.versions[meta.targetVersions[identifier]]!!.supportEmail),
                    costsPerBlock = listOf(meta.versions[meta.targetVersions[identifier]]!!.costPerBlock),
                    enableds = listOf(meta.versions[meta.targetVersions[identifier]]!!.Enabled)
                )
            }
        }
    }
}