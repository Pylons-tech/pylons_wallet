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
                if (it.isFile && it.extension == "json") {
                    ls.add(it.nameWithoutExtension)
                    println("Found ${it.nameWithoutExtension}")
                }
            }
            return ls
        }

        fun loadCookbook (fileName : String) : MetaCookbook {
            val path = Path.of(target!!.projectDir.absolutePath, "cookbook", "$fileName.json")
            val f = File(path.toUri())
            val stream = FileInputStream(f)
            val cb = try {
                klaxon.parse<MetaCookbook>(stream)
            } catch (e : IOException) {
                println("${f.nameWithoutExtension} is not a RecipeTool cookbook record!")
                null
            }
            stream.close()
            if (cb!!.id != fileName) throw Exception(
                "$fileName.json stores a cookbook record for ID ${cb.id}." +
                        "Filename should always be same as cookbook ID.")
            loadedCookbooks[fileName] = cb!!
            println("Loaded cookbook record $fileName")
            return cb
        }

        fun saveCookbook (cookbook: MetaCookbook) {
            val path = Path.of(target!!.projectDir.absolutePath, "cookbook", cookbook.id, ".json")
            val json = klaxon.toJsonString(cookbook)
            val file = File(path.toUri())
            if (file.exists())
                if (!file.canWrite()) file.setWritable(true)
            else file.createNewFile()
            file.writeText(json)
            println("Saved cookbook ${cookbook.id}")
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
        target = t
        if (IMulticore.config == null) {

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
        target!!.tasks.register("createAccount", CreateAccountTask::class.java) {
            it.group = "recipetool"
        }
        target!!.tasks.register("createRecipe", CreateRecipeTask::class.java) {
            it.group = "recipetool"
            it.dependsOn("createAccount")
        }
        target!!.tasks.register("createCookbook", CreateCookbookTask::class.java) {
            it.group = "recipetool"
            it.dependsOn("createAccount")
        }
        target!!.tasks.register("loadCookbook", LoadCookbookTask::class.java) {
            it.group = "recipetool"
            it.dependsOn("createAccount")
        }
        target!!.tasks.register("saveCookbook", SaveCookbookTask::class.java) {
            it.group = "recipetool"
            it.dependsOn("loadCookbook")
        }
        target!!.tasks.register("getCookbook", GetCookbookTask::class.java) {
            it.group = "recipetool"
            it.dependsOn("loadCookbook")
            it.finalizedBy("saveCookbook")
        }
        target!!.tasks.register("smartUpdateCookbook", SmartUpdateCookbookTask::class.java) {
            it.group = "recipetool"
            it.dependsOn("loadCookbook")
            it.finalizedBy("saveCookbook")
        }
    }
}