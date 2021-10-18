package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.targets.js.npm.SemVer
import tech.pylons.lib.klaxon
import tech.pylons.wallet.core.Core

abstract class SmartUpdateRecipeTask : DefaultTask()  {
    @TaskAction
    fun smartUpdateRecipe () {
        println("Loaded recipes: ${RecipeManagementPlugin.loadedRecipes.size}")
        val identifier = RecipeManagementPlugin.currentRemote.identifier()
        if (project.hasProperty("filepath")) {
            Core.current!!.getProfile()
            val meta = RecipeManagementPlugin.loadedRecipes[project.property("filepath") as String]!!
            if (SemVer.from(MetaRecipe.version(meta.remotes[identifier]!!)) <
                SemVer.from(meta.targetVersions[identifier]!!)) {
                Core.current!!.batchUpdateRecipe(
                    creators = listOf(meta.versions[meta.targetVersions[identifier]]!!.id),
                    cookbookIds = listOf(meta.versions[meta.targetVersions[identifier]]!!.cookbookId),
                    ids = listOf(meta.ids[identifier]!!),
                    names = listOf(meta.versions[meta.targetVersions[identifier]]!!.name),
                    descriptions = listOf(meta.versions[meta.targetVersions[identifier]]!!.description),
                    versions = listOf(meta.versions[meta.targetVersions[identifier]]!!.version),
                    coinInputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.coinInputs)),
                    itemInputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.itemInputs)),
                    entries = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.entries)),
                    outputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.outputs)),
                    blockIntervals = listOf(meta.versions[meta.targetVersions[identifier]]!!.blockInterval),
                    enableds = listOf(meta.versions[meta.targetVersions[identifier]]!!.enabled),
                    extraInfos = listOf("")
                )
            }
            if (meta.disabled[identifier]!!) {
                if (meta.remotes[identifier]!!.enabled) Core.current!!.batchDisableRecipe(listOf(meta.versions[meta.targetVersions[identifier]]!!.id))
            }
            else if (!meta.remotes[identifier]!!.enabled) Core.current!!.batchEnableRecipe(listOf(meta.versions[meta.targetVersions[identifier]]!!.id))

        }
        else {
            RecipeManagementPlugin.loadedRecipes.values.forEach { meta ->
                Core.current!!.getProfile()
//                if (meta.remotes.containsKey(identifier)) {
//                    if (SemVer.from(MetaRecipe.version(meta.remotes[identifier]!!)) <
//                        SemVer.from(meta.targetVersions[identifier]!!)) {
//                        Core.current!!.batchUpdateRecipe(
//                            cookbookIds = listOf(meta.versions[meta.targetVersions[identifier]]!!.cookbookId),
//                            ids = listOf(meta.ids[identifier]!!),
//                            names = listOf(meta.versions[meta.targetVersions[identifier]]!!.name),
//                            descriptions = listOf(meta.versions[meta.targetVersions[identifier]]!!.description),
//                            versions = listOf(meta.versions[meta.targetVersions[identifier]]!!.version),
//                            coinInputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.coinInputs)),
//                            itemInputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.itemInputs)),
//                            entries = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.entries)),
//                            outputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.outputs)),
//                            blockIntervals = listOf(meta.versions[meta.targetVersions[identifier]]!!.blockInterval),
//                            enableds = listOf(meta.versions[meta.targetVersions[identifier]]!!.enabled),
//                            extraInfos = listOf("")
//                        )
//                    }
//                    if (meta.disabled[identifier]!!) {
//                        if (!meta.remotes[identifier]!!.disabled) Core.current!!.batchDisableRecipe(listOf(meta.versions[meta.targetVersions[identifier]]!!.id))
//                    }
//                    else if (meta.remotes[identifier]!!.disabled) Core.current!!.batchEnableRecipe(listOf(meta.versions[meta.targetVersions[identifier]]!!.id))
//                }
//                else {
//                    Core.current!!.batchCreateRecipe(
//                        cookbooks = listOf(meta.versions[meta.targetVersions[identifier]]!!.cookbookId),
//                        names = listOf(meta.versions[meta.targetVersions[identifier]]!!.name),
//                        descriptions = listOf(meta.versions[meta.targetVersions[identifier]]!!.description),
//                        blockIntervals = listOf(meta.versions[meta.targetVersions[identifier]]!!.blockInterval),
//                        coinInputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.coinInputs)),
//                        itemInputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.itemInputs)),
//                        outputTables = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.entries)),
//                        outputs = listOf(klaxon.toJsonString(meta.versions[meta.targetVersions[identifier]]!!.outputs)),
//                        extraInfos = listOf("")
//                    )
//                }
            }
        }
    }
}