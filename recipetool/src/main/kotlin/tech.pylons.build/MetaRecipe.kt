package tech.pylons.build

import org.jetbrains.kotlin.gradle.targets.js.npm.SemVer
import tech.pylons.lib.types.tx.recipe.Recipe


data class MetaRecipe (
    val name : String,
    val cookbook : String,
    var latestVersion : String,
    var ids : MutableMap<String, String> = mutableMapOf(),
    var versions : MutableMap<String, Recipe> = mutableMapOf(),
    var remotes : MutableMap<String, Recipe> = mutableMapOf(),
    var targetVersions : MutableMap<String, String> = mutableMapOf(),
    var disabled : MutableMap<String, Boolean> = mutableMapOf()
) {
    companion object {
        fun version (r : Recipe) : String {
            return (r.description.split('|').first())
        }
    }

    fun addRemoteState(r : Recipe) {
        remotes[RecipeManagementPlugin.currentRemote.identifier()] = r
        if (versions.containsKey(version(r))) println(
            "WARNING: overwriting local recipe version with remote copy. " +
                    "RecipeTool does not presently provide any means of distinguishing more or" +
                    "less canonical versions of recipes with the same version string from one another." +
                    "This will probably present problems for larger projects.")
        versions[version(r)] = r
        if (SemVer.from(version(r)) > SemVer.from(latestVersion)) latestVersion = version(r)
        if (SemVer.from(version(r)) >=
            SemVer.from(targetVersions[RecipeManagementPlugin.currentRemote.identifier()]!!)) {
            disabled[RecipeManagementPlugin.currentRemote.identifier()] = r.disabled
        }
        ids[RecipeManagementPlugin.currentRemote.identifier()] = r.id
    }
}