package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import tech.pylons.wallet.core.Core

/**
 * Gradle task: Creates an account on current remote using current credentials, if none exists.
 *
 */
abstract class CreateAccountTask : DefaultTask() {

    @TaskAction
    fun createAccountIfNeeded() {
        println("CreateAccountTask should see if you need to make an account for these keys" +
                "and do it if so, but it doesn't yet, so I hope they're registered!")
        //if (Core.current!!.userProfile.)
    }
}