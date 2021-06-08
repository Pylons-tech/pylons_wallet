package tech.pylons.build

import com.google.common.base.Ascii
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.spongycastle.jce.provider.BouncyCastleProvider
import pylons.Pylons
import tech.pylons.ipc.FakeIPC
import tech.pylons.ipc.FakeUI
import tech.pylons.ipc.IPCLayer
import tech.pylons.ipc.UILayer
import tech.pylons.lib.core.ICore
import tech.pylons.lib.klaxon
import tech.pylons.lib.logging.Logger
import tech.pylons.lib.types.Backend
import tech.pylons.lib.types.Config
import tech.pylons.lib.types.UserData
import tech.pylons.wallet.core.Core
import tech.pylons.wallet.core.Multicore
import java.io.StringReader
import java.security.Security

class ManageRecipeTask : DefaultTask() {
    companion object {
        val cfg = Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317"))
        private var core : ICore? = null

        private fun bootstrap () {
            // Q: can we actually reuse walletcore for multiple operations?
            // should we?
            println("Bootstrapping walletcore for recipe management")
            IPCLayer.implementation = FakeIPC()
            UILayer.implementation = FakeUI()
            Security.addProvider(BouncyCastleProvider())
            Multicore.enable(cfg)
            // where the hell do we get keys from?
            // (how does this even work atm?)
            // and how do we conform that to the realities of 'this is a gradle task'
        }
    }
    @Input
    val node : String? = null
    @Input
    val recipe : String? = null

    fun manage() {
        bootstrap()
        val recipe = Pylons.Recipe.parseFrom(recipe!!.toByteArray(Charsets.US_ASCII))
        // if the recipe exists: update it
        // if the recipe doesn't: create it
    }

    private fun create (rcp : Pylons.Recipe) {
        //Core.current!!.batchCreateRecipe(listOf(rcp.name), listOf(rcp.cookbookID), listOf(rcp.description),
        //listOf(rcp.blockInterval), listOf())

    }

}