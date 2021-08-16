package tech.pylons.build

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.Parser.Companion.default
import org.apache.tuweni.bytes.Bytes32
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.utils.`is`
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.spongycastle.util.encoders.Hex
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
import java.nio.file.Files
import java.nio.file.Path
import java.security.Security
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.name

class RecipeManagementPlugin : Plugin<Project> {
    companion object {
        lateinit var devConfig: DevConfig
        lateinit var currentRemote : Remote

        val loadedCookbooks : MutableMap<String, MetaCookbook> = mutableMapOf()
        val loadedRecipes : MutableMap<String, MetaRecipe> = mutableMapOf()

        var target : Project? = null

        fun loadDevConfig () {
            val path = Path.of(target!!.projectDir.absolutePath, "recipetool.json")
            val file = File(path.toUri())
            if (file.exists()) {
                devConfig = klaxon.parse<DevConfig>(file)!!
            }
            else {
                devConfig = DevConfig("Developer", "", "local")
                file.createNewFile()
                file.writeText(klaxon.toJsonString(devConfig))
            }
            currentRemote = devConfig.remoteConfig()
        }

        fun shittyKeys () : PylonsSECP256K1.KeyPair? {
            val path = Path.of(target!!.projectDir.absolutePath, "dev-keys")
            val file = File(path.toUri())
            val text = file.readText()
            return if (file.exists()) PylonsSECP256K1.KeyPair.fromSecretKey(
                PylonsSECP256K1.SecretKey.fromBytes(Bytes32.wrap(Hex.decode(text))))
            else null
        }


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

        @OptIn(ExperimentalPathApi::class)
        fun enumerateRecipeRecords () : List<String> {
            val ls = mutableListOf<String>()
            val path = Path.of(target!!.projectDir.absolutePath, "recipe")
            Files.walk(path, 2).skip(1).forEach {
                val file = it.toFile()
                if (file.isFile && file.extension == "json") {
                    ls.add("${it.parent.name}/${file.nameWithoutExtension}")
                    println("Found ${ls.last()}")
                }
            }
            println(ls.size)
            return ls
        }

        fun loadCookbook (fileName : String) : MetaCookbook {
            val path = Path.of(target!!.projectDir.absolutePath, "cookbook", "$fileName.json")
            val f = File(path.toUri())
            val stream = FileInputStream(f)
            val cb = try {
                klaxon.parse<MetaCookbook>(stream)
            } catch (e : Exception) {
                println("${f.nameWithoutExtension} is not a RecipeTool cookbook record!\nCaused by: $e")
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

        fun loadRecipe (fileName : String) : MetaRecipe {
            val splut = fileName.split('/', '\\')
            if (splut.size != 2) throw Exception("Expected format of 'cookbook/recipe', got $fileName instead")
            val path = Path.of(target!!.projectDir.absolutePath, "recipe", splut[0], "${splut[1]}.json")
            val f = File(path.toUri())
            val stream = FileInputStream(f)
            val r = try {
                klaxon.parse<MetaRecipe>(stream)
            } catch (e : Exception) {
                println("${f.nameWithoutExtension} is not a RecipeTool recipe record!\nCaused by: $e")
                null
            }
            stream.close()
            if (r!!.name != splut[1] || r!!.cookbook != splut[0]) throw Exception(
                "$fileName.json stores a recipe record for ${r.cookbook}/${r.name}." +
                        "Filename should always be same as recipe coords.")
            loadedRecipes[fileName] = r!!
            println("Loaded recipe record $fileName")
            return r
        }

        fun saveCookbook (cookbook: MetaCookbook) {
            val path = Path.of(target!!.projectDir.absolutePath, "cookbook", "${cookbook.id}.json")
            val sb = StringBuilder(klaxon.toJsonString(cookbook))
            val json = (default(  ).parse(sb) as JsonObject).toJsonString(true)
            val file = File(path.toUri())
            if (file.exists())
                if (!file.canWrite()) file.setWritable(true)
            else file.createNewFile()
            file.writeText(json)
            println("Saved cookbook ${cookbook.id}.json")
        }

        fun saveRecipe (recipe : MetaRecipe) {
            val path = Path.of(target!!.projectDir.absolutePath, "cookbook", recipe.cookbook, "${recipe.name}.json")
            val sb = StringBuilder(klaxon.toJsonString(recipe))
            val json = (default(  ).parse(sb) as JsonObject).toJsonString(true)
            val file = File(path.toUri())
            if (file.exists())
                if (!file.canWrite()) file.setWritable(true)
                else file.createNewFile()
            file.writeText(json)
            println("Saved recipe ${recipe.cookbook}/${recipe.name}}.json")
        }

        fun getRecipeFromPath (fileName : String) : Recipe {
            val path = Path.of(target!!.projectDir.absolutePath, "recipe", fileName)
            val f = File(path.toUri())
            val stream = FileInputStream(f)
            val recipe = try {
                klaxon.parse<Recipe>(stream)
            } catch (e : IOException) {
                println("${f.name} is not a valid recipe!\nCaused by: $e")
                null
            }
            stream.close()
            return recipe!!
        }
    }

    override fun apply(t: Project) {
        target = t
        loadDevConfig()
        if (IMulticore.config == null) {
            IPCLayer.implementation = FakeIPC()
            UILayer.implementation = FakeUI()
            Security.addProvider(BouncyCastleProvider())
            Multicore.enable(Config(currentRemote.backend, currentRemote.chainId, true, listOf()))
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
            it.finalizedBy("getCookbook")
        }
        target!!.tasks.register("loadRecipe", LoadRecipeTask::class.java) {
            it.group = "recipetool"
            it.dependsOn("createAccount")
        }
        target!!.tasks.register("saveRecipe", SaveRecipeTask::class.java) {
            it.group = "recipetool"
            it.dependsOn("loadRecipe")
        }
        target!!.tasks.register("getRecipe", GetRecipeTask::class.java) {
            it.group = "recipetool"
            it.dependsOn("loadRecipe", "getCookbook")
            it.finalizedBy("saveRecipe")
        }
        target!!.tasks.register("smartUpdateRecipe", SmartUpdateRecipeTask::class.java) {
            it.group = "recipetool"
            it.dependsOn("loadRecipe")
            it.finalizedBy("getRecipe")
        }
    }
}