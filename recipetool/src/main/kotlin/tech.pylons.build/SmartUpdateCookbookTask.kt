package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.targets.js.npm.SemVer
import tech.pylons.wallet.core.Core

abstract class SmartUpdateCookbookTask : DefaultTask()  {
    @TaskAction
    fun smartUpdateCookbook () {
        val hash = RecipeManagementPlugin.currentRemote.hash()
        if (project.hasProperty("filepath")) {
            val meta = RecipeManagementPlugin.loadedCookbooks[project.property("filepath") as String]!!
            if (SemVer.from(meta.remotes[hash]!!.version) <
                SemVer.from(meta.targetVersions[hash]!!))
                Core.current!!.batchUpdateCookbook(
                    ids = listOf(meta.versions[meta.targetVersions[hash]]!!.id),
                    names = listOf(meta.versions[meta.targetVersions[hash]]!!.name),
                    developers = listOf(meta.versions[meta.targetVersions[hash]]!!.developer),
                    descriptions = listOf(meta.versions[meta.targetVersions[hash]]!!.description),
                    supportEmails = listOf(meta.versions[meta.targetVersions[hash]]!!.supportEmail),
                    versions = listOf(meta.versions[meta.targetVersions[hash]]!!.version)
                )
        }
        else {
            RecipeManagementPlugin.loadedCookbooks.values.forEach { meta ->
                if (SemVer.from(meta.remotes[hash]!!.version) <
                    SemVer.from(meta.targetVersions[hash]!!))
                        Core.current!!.batchUpdateCookbook(
                            ids = listOf(meta.versions[meta.targetVersions[hash]]!!.id),
                            names = listOf(meta.versions[meta.targetVersions[hash]]!!.name),
                            developers = listOf(meta.versions[meta.targetVersions[hash]]!!.developer),
                            descriptions = listOf(meta.versions[meta.targetVersions[hash]]!!.description),
                            supportEmails = listOf(meta.versions[meta.targetVersions[hash]]!!.supportEmail),
                            versions = listOf(meta.versions[meta.targetVersions[hash]]!!.version)
                        )
            }
        }
    }
}