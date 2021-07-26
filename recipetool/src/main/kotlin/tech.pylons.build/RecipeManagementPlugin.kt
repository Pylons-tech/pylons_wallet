package tech.pylons.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.spongycastle.jce.provider.BouncyCastleProvider
import tech.pylons.ipc.FakeIPC
import tech.pylons.ipc.FakeUI
import tech.pylons.ipc.IPCLayer
import tech.pylons.ipc.UILayer
import tech.pylons.lib.core.IMulticore
import tech.pylons.lib.klaxon
import tech.pylons.lib.types.Backend
import tech.pylons.lib.types.Config
import tech.pylons.lib.types.Cookbook
import tech.pylons.lib.types.PylonsSECP256K1
import tech.pylons.lib.types.tx.recipe.Recipe
import tech.pylons.wallet.core.Multicore
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Path
import java.security.Security

class RecipeManagementPlugin : Plugin<Project> {
    companion object {
        var currentRemote : Remote = Remote(
            "pylonschain",
            "127.0.0.1:1317",
            "127.0.0.1:1317")

        val loadedCookbooks : MutableMap<String, MetaCookbook> = mutableMapOf()

        var target : Project? = null

        fun enumerateCookbookRecords () : List<String> {
            val ls = mutableListOf<String>()
            val path = Path.of(target!!.projectDir.absolutePath, "cookbook")
            File(path.toUri()).walk().forEach {
                ls.add(it.name)
                println("Found ${it.name}")
            }
            return ls
        }

        fun loadCookbook (fileName : String) : MetaCookbook {
            val path = Path.of(target!!.projectDir.absolutePath, "cookbook", fileName)
            val f = File(path.toUri())
            val stream = FileInputStream(f)
            val cb = try {
                klaxon.parse<MetaCookbook>(stream)
            } catch (e : IOException) {
                println("${f.name} is not a RecipeTool cookbook record!")
                null
            }
            stream.close()
            loadedCookbooks[fileName] = cb!!
            println("Loaded cookbook record $fileName")
            return cb
        }

        fun getRecipeFromPath (fileName : String) : Recipe {
            val path = Path.of(target!!.projectDir.absolutePath, "recipe", fileName)
            val f = File(path.toUri())
            val stream = FileInputStream(f)
            val recipe = try {
                klaxon.parse<Recipe>(stream)
            } catch (e : IOException) {
                println("${f.name} is not a valid recipe!")
                null
            }
            stream.close()
            return recipe!!
        }
    }

    override fun apply(t: Project) {
        if (IMulticore.config == null) {
            target = t
            IPCLayer.implementation = FakeIPC()
            UILayer.implementation = FakeUI()
            Security.addProvider(BouncyCastleProvider())
            Multicore.enable(Config(Backend.TESTNET, "pylons-testnet", true, listOf()))
            // TODO: There should be a proper system in place for handling keydata,
            // but the build-out would take too much time atm, so we're gonna do something shitty.
            if (!File(target!!.projectDir.absolutePath,"dev-keys").exists()) {
                val kp = PylonsSECP256K1.KeyPair.random()
                kp.store(Path.of(target!!.projectDir.absolutePath, "dev-keys"))
            }
            Multicore.addCore(PylonsSECP256K1.KeyPair.load(Path.of(target!!.projectDir.absolutePath,"dev-keys")))
        }
        target!!.tasks.create("createRecipe", CreateRecipeTask::class.java)
        target!!.tasks.create("createCookbook", CreateCookbookTask::class.java)
        target!!.tasks.create("loadCookbook", LoadCookbookTask::class.java)
        target!!.tasks.create("getCookbook", GetCookbookTask::class.java).dependsOn(
            target!!.task("loadCookbook"))
        target!!.tasks.create("smartUpdateCookbook", SmartUpdateCookbookTask::class.java).dependsOn(
            target!!.task("getCookbook")
        )

/*        target!!.task("enumerateRecipes") {
            val path = Path.of(target!!.projectDir.absolutePath, "recipe")
            println("Enumerating recipes in $path")
            getRecipes(path.toString()).forEach {
                println("Found ${it.cookbookID} / ${it.name}")
            }
        }
        target!!.task("updateManagedRecipes") {
            val path = Path.of(target!!.projectDir.absolutePath, "recipe")
            println("Enumerating recipes in $path")
            getRecipes(path.toString()).forEach {
                println("Found ${it.cookbookID} / ${it.name}")
                println(Core.current?.getRecipe(it.id)?.name)
                //Core.current!!.batchCreateRecipe(listOf(it.name))
            }
        }*/
    }

/*    private fun getRecipes (path : String) : List<Pylons.Recipe> {
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
    }*/
}