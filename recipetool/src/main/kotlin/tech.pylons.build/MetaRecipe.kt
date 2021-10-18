package tech.pylons.build

import org.jetbrains.kotlin.gradle.targets.js.npm.SemVer
import tech.pylons.lib.types.tx.recipe.Recipe

/**
 * Record representing all known local and remote versions of a recipe.
 */
data class MetaRecipe (
    /**
     * Name of the recipe. Name is used in determining recipe coords, and does not change
     * across versions or platforms.
     */
    val name : String,
    /**
     * Cookbook the recipe is in. Cookbook is used in determining recipe coords, and does not change
     * across versions or platforms.
     */
    val cookbook : String,
    /**
     * The latest version of the recipe that exists in any form across this record and all
     * known remotes.
     */
    var latestVersion : String,
    /**
     * Map of all remotes the recipe exists on to the ID it exists under on that remote.
     * Recipe ID is determined at creation time, so we can't know it in advance and it
     * won't likely be the same across multiple chains.
     */
    var ids : MutableMap<String, String> = mutableMapOf(),
    /**
     * Map of all known versions to the current state of the recipe as of that version.
     * Certain parameters (node version, recipe ID) may vary across remotes; the
     * "canonical" version of each version will always be one that's been retrieved
     * from a chain, if available, but chain-dependent parameters will still sometimes
     * be inconsistent between entries. This is fine.
     */
    var creators : MutableMap<String, Recipe> = mutableMapOf(),
    var versions : MutableMap<String, Recipe> = mutableMapOf(),
    /**
     * Map of all remotes the recipe exists on to the current state of the recipe on that
     * remote.
     */
    var remotes : MutableMap<String, Recipe> = mutableMapOf(),
    /**
     * Map of all remotes to the version of the recipe that should be deployed to that
     * remote. Note that if the target version is behind the actual version, we will just
     * let it be; we can't un-deploy a recipe, and we shouldn't be able to deploy something
     * of an earlier version over something of a later version, so there's nothing for us
     * to do in that case.
     */
    var targetVersions : MutableMap<String, String> = mutableMapOf(),
    /**
     * Map of all remotes to whether or not the recipe should be disabled on that remote.
     * At present this has to be handled separately from other recipe state or versioning
     * info, which is why it's a top-level map.
     */
    var disabled : MutableMap<String, Boolean> = mutableMapOf()
) {
    companion object {
        /**
         * Get the current version of the recipe, as a SemVer string.
         * Recipe versioning is a hack; the field doesn't actually exist,
         * so we use a format of '{version}|{description}' in the recipe
         * description as a convention. Recipes meant to be processed by
         * recipetool must adhere to this format.
         */
        fun version (r : Recipe) : String {
            return (r.description.split('|').first())
        }
    }

    /**
     * Internal use: Add state to a MetaRecipe from a recipe obtained from a remote.
     */
    internal fun addRemoteState(r : Recipe) {
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
            disabled[RecipeManagementPlugin.currentRemote.identifier()] = r.enabled
        }
        ids[RecipeManagementPlugin.currentRemote.identifier()] = r.id
    }
}