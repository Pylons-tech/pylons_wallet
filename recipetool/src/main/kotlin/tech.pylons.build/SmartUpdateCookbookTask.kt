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
                    ids = listOf(meta.versions[meta.targetVersions[identifier]]!!.id),
                    names = listOf(meta.versions[meta.targetVersions[identifier]]!!.name),
                    developers = listOf(meta.versions[meta.targetVersions[identifier]]!!.developer),
                    descriptions = listOf(meta.versions[meta.targetVersions[identifier]]!!.description),
                    supportEmails = listOf(meta.versions[meta.targetVersions[identifier]]!!.supportEmail),
                    versions = listOf(meta.versions[meta.targetVersions[identifier]]!!.version)
                )
        }
        else {
            RecipeManagementPlugin.loadedCookbooks.values.forEach { meta ->
                Core.current!!.getProfile()
                if (meta.remotes.containsKey(identifier) && SemVer.from(meta.remotes[identifier]!!.version) <
                    SemVer.from(meta.targetVersions[identifier]!!)) {
                    Core.current!!.batchUpdateCookbook(
                        ids = listOf(meta.versions[meta.targetVersions[identifier]]!!.id),
                        names = listOf(meta.versions[meta.targetVersions[identifier]]!!.name),
                        developers = listOf(meta.versions[meta.targetVersions[identifier]]!!.developer),
                        descriptions = listOf(meta.versions[meta.targetVersions[identifier]]!!.description),
                        supportEmails = listOf(meta.versions[meta.targetVersions[identifier]]!!.supportEmail),
                        versions = listOf(meta.versions[meta.targetVersions[identifier]]!!.version)
                    )
                }
                else if (SemVer.from(meta.remotes[identifier]?.version ?: "0.0.0") !=
                    SemVer.from(meta.targetVersions[identifier]!!)) Core.current!!.batchCreateCookbook(
                    ids = listOf(meta.versions[meta.targetVersions[identifier]]!!.id),
                    names = listOf(meta.versions[meta.targetVersions[identifier]]!!.name),
                    developers = listOf(meta.versions[meta.targetVersions[identifier]]!!.developer),
                    descriptions = listOf(meta.versions[meta.targetVersions[identifier]]!!.description),
                    supportEmails = listOf(meta.versions[meta.targetVersions[identifier]]!!.supportEmail),
                    versions = listOf(meta.versions[meta.targetVersions[identifier]]!!.version),
                    costsPerBlock = listOf(meta.versions[meta.targetVersions[identifier]]!!.costPerBlock))
            }
        }
    }
}