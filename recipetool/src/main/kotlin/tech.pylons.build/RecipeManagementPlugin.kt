package tech.pylons.build

import com.beust.klaxon.Klaxon
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.spongycastle.jce.provider.BouncyCastleProvider
import pylons.Pylons
import tech.pylons.ipc.FakeIPC
import tech.pylons.ipc.FakeUI
import tech.pylons.ipc.IPCLayer
import tech.pylons.ipc.UILayer
import tech.pylons.lib.core.IMulticore
import tech.pylons.lib.logging.Logger
import tech.pylons.lib.types.Backend
import tech.pylons.lib.types.Config
import tech.pylons.wallet.core.Core
import tech.pylons.wallet.core.Multicore
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.security.Security

class RecipeManagementPlugin : Plugin<Project> {
    val klaxon = Klaxon()

    override fun apply(target: Project) {
        if (IMulticore.config == null) {
            IPCLayer.implementation = FakeIPC()
            UILayer.implementation = FakeUI()
            Security.addProvider(BouncyCastleProvider())
            Multicore.enable(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317")))
            // how do we get our keys?
        }

        //target.extensions.add("recipe-management-plugin", )

        target.task("enumerateRecipes") {
            val path = Path.of(target.projectDir.absolutePath, "recipe")
            println("Enumerating recipes in $path")
            getRecipes(path.toString()).forEach {
                println("Found ${it.cookbookID} / ${it.name}")
            }
        }

        target.task("updateManagedRecipes") {
            val path = Path.of(target.projectDir.absolutePath, "recipe")
            println("Enumerating recipes in $path")
            getRecipes(path.toString()).forEach {
                println("Found ${it.cookbookID} / ${it.name}")
                println(Core.current?.getRecipe(it.id)?.name)
                //Core.current!!.batchCreateRecipe(listOf(it.name))
            }
        }
    }

    private fun getRecipes (path : String) : List<Pylons.Recipe> {
        val list = mutableListOf<Pylons.Recipe>()
        File(path).walk().forEach {
            println(it.absolutePath)
            if (it.extension == "json") {
                val stream = FileInputStream(it)
                val recipe = try {
                    Pylons.Recipe.parseFrom(stream)
                } catch (e : IOException) {
                    println("${it.name} is not a valid recipe!")
                    null
                }
                stream.close()
                if (recipe != null) list.add(recipe)
            }
        }
        return list
    }
}