package tech.pylons.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import tech.pylons.lib.core.ICore
import tech.pylons.lib.types.Backend
import tech.pylons.lib.types.Config
import tech.pylons.lib.types.UserData
import tech.pylons.wallet.core.Core

class ManageRecipeTask : DefaultTask() {
    companion object {
        val cfg = Config(Backend.LIVE_DEV, listOf())
        private var core : ICore? = null

        private fun bootstrap (val key : String) {
            // Q: can we actually reuse walletcore for multiple operations?
            // should we?
            println("Bootstrapping walletcore for recipe management")
            // Get keys: for now,
            // Initialize core
            con
            core = Core(cfg)
            core!!.start()
        }
    }
    @Input
    val node : String? = null
    @Input
    val recipe : String? = null

    private fun create () {

    }

}