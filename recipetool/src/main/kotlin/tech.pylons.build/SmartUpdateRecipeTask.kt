package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.targets.js.npm.SemVer
import tech.pylons.lib.klaxon
import tech.pylons.wallet.core.Core

abstract class SmartUpdateRecipeTask : DefaultTask()  {
    @TaskAction
    fun smartUpdateCookbook () {
        val identifier = RecipeManagementPlugin.currentRemote.identifier()
        if (project.hasProperty("filepath")) {
            Core.current!!.getProfile()
            val meta = RecipeManagementPlugin.loadedRecipes[project.property("filepath") as String]!!
            if (SemVer.from(MetaRecipe.version(meta.remotes[identifier]!!)) <
                SemVer.from(meta.targetVersions[identifier]!!)) {
                Core.current!!.batchUpdateRecipe(
                    ids = listOf(meta.ids[identifier]!!),
                    cookbooks = listOf(meta.versions[meta.targetVersions[identifier]]!!.cookbookId),
                    names = listOf(meta.versions[meta.targetVersions[identifier]]!!.name),
                    descriptions = listOf(meta.versions[meta.targetVersions[identifier]]!!.description),
                    blockIntervals = listOf(meta.versions[meta.targetVersions[identifier]]!!.blockInterval),
                    coinInputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.coinInputs)),
                    itemInputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.itemInputs)),
                    outputTables = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.entries)),
                    outputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.outputs))
                )
            }
            if (meta.disabled[identifier]!!) {
                if (!meta.remotes[identifier]!!.disabled) Core.current!!.batchDisableRecipe(listOf(meta.versions[meta.targetVersions[identifier]]!!.id))
            }
            else if (meta.remotes[identifier]!!.disabled) Core.current!!.batchEnableRecipe(listOf(meta.versions[meta.targetVersions[identifier]]!!.id))

        }
        else {
            RecipeManagementPlugin.loadedRecipes.values.forEach { meta ->
                Core.current!!.getProfile()
                if (meta.remotes.containsKey(identifier)) {
                    if (SemVer.from(MetaRecipe.version(meta.remotes[identifier]!!)) <
                        SemVer.from(meta.targetVersions[identifier]!!)) {
                        Core.current!!.batchUpdateRecipe(
                            ids = listOf(meta.ids[identifier]!!),
                            cookbooks = listOf(meta.versions[meta.targetVersions[identifier]]!!.cookbookId),
                            names = listOf(meta.versions[meta.targetVersions[identifier]]!!.name),
                            descriptions = listOf(meta.versions[meta.targetVersions[identifier]]!!.description),
                            blockIntervals = listOf(meta.versions[meta.targetVersions[identifier]]!!.blockInterval),
                            coinInputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.coinInputs)),
                            itemInputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.itemInputs)),
                            outputTables = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.entries)),
                            outputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.outputs)),
                            extraInfos = listOf()
                        )
                    }
                    if (meta.disabled[identifier]!!) {
                        if (!meta.remotes[identifier]!!.disabled) Core.current!!.batchDisableRecipe(listOf(meta.versions[meta.targetVersions[identifier]]!!.id))
                    }
                    else if (meta.remotes[identifier]!!.disabled) Core.current!!.batchEnableRecipe(listOf(meta.versions[meta.targetVersions[identifier]]!!.id))
                }
                else {
                    Core.current!!.batchCreateRecipe(
                        cookbooks = listOf(meta.versions[meta.targetVersions[identifier]]!!.cookbookId),
                        names = listOf(meta.versions[meta.targetVersions[identifier]]!!.name),
                        descriptions = listOf(meta.versions[meta.targetVersions[identifier]]!!.description),
                        blockIntervals = listOf(meta.versions[meta.targetVersions[identifier]]!!.blockInterval),
                        coinInputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.coinInputs)),
                        itemInputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.itemInputs)),
                        outputTables = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.entries)),
                        outputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.outputs)),
                        extraInfos = listOf()
                    )
                }
            }
        }
    }
}