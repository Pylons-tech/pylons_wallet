package tech.pylons.build

import org.jetbrains.kotlin.gradle.targets.js.npm.SemVer
import tech.pylons.lib.types.Cookbook

/**
 * Record representing all known local and remote versions of a cookbook.
 */
data class MetaCookbook (
    /**
     * Cookbook ID never changes, and lets us maintain a consistent identity between
     * remotes and versions.
     */
    val id : String,
    var latestVersion : String,
    /**
     * Map of SemVer strings to the cookbook as of that version.
     */
    var versions : MutableMap<String, Cookbook> = mutableMapOf(),

    /**
     * Map of remote hashes to the last known state of the cookbook on that remote.
     * If any of these are outdated, they should be updated w/ the latest state of
     * the cookbook as soon as they're available. If any of them are ahead of our
     * local copy, we should update it based on them.
     */
    var remotes : MutableMap<String, Cookbook> = mutableMapOf(),
    var targetVersions : MutableMap<String, String> = mutableMapOf()
) {
    fun addRemoteState(cb : Cookbook) {
        remotes[RecipeManagementPlugin.currentRemote.hash()] = cb
        if (versions.containsKey(cb.version)) println(
                    "WARNING: overwriting local cookbook version with remote copy. " +
                    "RecipeTool does not presently provide any means of distinguishing more or" +
                    "less canonical versions of cookbooks with the same version string from one another." +
                    "This will probably present problems for larger projects.")
        versions[cb.version] = cb
        if (SemVer.from(cb.version) > SemVer.from(latestVersion)) latestVersion = cb.version
    }
}