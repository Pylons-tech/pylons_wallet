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
    /**
     * The latest version of the cookbook that exists in any form across this record and all
     * known remotes.
     */
    var latestVersion : String,
    /**
     * Map of SemVer strings to the cookbook as of that version.
     */
    var versions : MutableMap<String, Cookbook> = mutableMapOf(),
    /**
     * Map of all remotes the cookbook exists on to the current state of the cookbook on that
     * remote.
     */
    var remotes : MutableMap<String, Cookbook> = mutableMapOf(),
    /**
     * Map of all remotes to the version of the cookbook that should be deployed to that
     * remote. Note that if the target version is behind the actual version, we will just
     * let it be; we can't un-deploy a cookbook, and we shouldn't be able to deploy something
     * of an earlier version over something of a later version, so there's nothing for us
     * to do in that case.
     */
    var targetVersions : MutableMap<String, String> = mutableMapOf()
) {
    /**
     * Internal use: Add state to a MetaCookbook from a cookbook obtained from a remote.
     */
    fun addRemoteState(cb : Cookbook) {
        remotes[RecipeManagementPlugin.currentRemote.identifier()] = cb
        if (versions.containsKey(cb.version)) println(
                    "WARNING: overwriting local cookbook version with remote copy. " +
                    "RecipeTool does not presently provide any means of distinguishing more or" +
                    "less canonical versions of cookbooks with the same version string from one another." +
                    "This will probably present problems for larger projects.")
        versions[cb.version] = cb
        if (SemVer.from(cb.version) > SemVer.from(latestVersion)) latestVersion = cb.version
    }
}