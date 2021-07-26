package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class SmartUpdateCookbookTask : DefaultTask()  {
    @TaskAction
    fun smartUpdateCookbook () {
        println("smart update isn't live yet")
    }
}