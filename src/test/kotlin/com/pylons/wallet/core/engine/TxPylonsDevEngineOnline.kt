package com.pylons.wallet.core.engine

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.fixtures.basicItemOutput
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.jsonModel.CoinInput
import com.pylons.wallet.core.types.jsonModel.CoinOutput
import com.pylons.wallet.core.types.jsonModel.ItemOutput
import com.pylons.wallet.core.types.jsonModel.WeightedParamList
import org.junit.jupiter.api.MethodOrderer

import java.util.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TxPylonsDevEngineOnline {
    private fun getCookbookIfOneExists (engine: TxPylonsDevEngine) : String {
        val cb = engine.listCookbooks()
        return when (cb.isNotEmpty()) {
            true -> cb[0].id
            false -> fail("No cookbooks exist on chain belonging to current address. This test cannot continue.")
        }
    }

    private fun getRecipeIfOneExists (engine: TxPylonsDevEngine) : String {
        val r = engine.listRecipes()
        return when (r.isNotEmpty()) {
            true -> r[0].id
            false -> fail("No recipes exist on chain belonging to current address. This test cannot continue.")
        }
    }

    private fun engineSetup (key : String? = null) : TxPylonsDevEngine {
        Core.start(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317")), "")
        val engine = Core.engine as TxPylonsDevEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        if (key != null) {
            UserData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to key)
            engine.cryptoHandler.importKeysFromUserData()
        }
        else engine.cryptoHandler.generateNewKeys()
        Core.userProfile = Profile(engine.generateCredentialsFromKeys(), mutableMapOf(), mutableMapOf(), mutableListOf())
        return engine
    }

    private fun checkIfRecipeExists (engine: TxPylonsDevEngine, recipeName: String, cookbook : String) {
        val recipes = engine.listRecipes()
        var recipe : Recipe? = null
        for (it : Recipe in recipes) { if (it.name == recipeName  && it.cookbook == cookbook) { recipe = it; break } }
        assertNotNull(recipe, "could not find recipe $recipeName in cookbook $cookbook")
        println(recipe?.name)
    }

    private fun basicTxTestFlow (txFun : (TxPylonsDevEngine) -> Transaction) = basicTxTestFlow(txFun, null)

    private fun basicTxTestFlow (txFun : (TxPylonsDevEngine) -> Transaction, followUp : ((TxPylonsDevEngine,  String) -> Unit)?) {
        val engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        println("getting profile state...")
        engine.getOwnBalances()
        var oldSequence = (Core.userProfile!!.credentials as TxPylonsEngine.Credentials).sequence
        println("building tx...")
        val tx = txFun(engine)
        println("submitting tx...")
        tx.submit()
        println("waiting for tx to resolve")
        while (true) {
            if (tx.state == Transaction.State.TX_NOT_YET_SENT) Thread.sleep(5000)
            else break
        }
        println("Waiting 5 seconds to allow chain to catch up")
        Thread.sleep(5000)
        engine.getOwnBalances()
        assertTrue((Core.userProfile!!.credentials as TxPylonsEngine.Credentials).sequence > oldSequence)
        assertEquals(Transaction.State.TX_ACCEPTED, tx.state)
        println("ok!")
        followUp?.invoke(engine, tx.id!!)
    }

    @Order(0)
    @Test
    fun getsPylons () {
        basicTxTestFlow { it.getPylons(500000) }
    }

    @Order(1)
    @Test
    fun sendsPylons () {
        basicTxTestFlow { it.sendPylons(1, "cosmos1hetxt4zc6kzq5ctepn9lz75jd5r4pkku0m5qch") }
    }

    @Order(2)
    @Test
    fun createsCookbook () {
        basicTxTestFlow { it.createCookbook("blyyah ${Random().nextInt()}", "tst",
                "this is a description for a test flow cookbook i guess",
                "1.0.0", "fake@example.com", 0, 50) }
    }

    @Order(3)
    @Test
    fun getsCookbooks () {
        var engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        var a = engine.listCookbooks()
    }

    @Order(4)
    @Test
    fun updatesCookbook () {
        basicTxTestFlow { it.updateCookbook(getCookbookIfOneExists(it), "tst",
                "this is a description for updatescookbook test", "1.0.0", "example@example.com") }
    }

    @Order(5)
    @Test
    fun createsRecipe () {
        val name = "wood ${Random().nextInt()}"
        basicTxTestFlow(
                { it.createRecipe(Core.userProfile!!.credentials.address, name, getCookbookIfOneExists(it),
                        "fooBar description blahhhh", 0, listOf(CoinInput("pylon", 2)), listOf(),
                        WeightedParamList(listOf(CoinOutput("wood", 999, 1)), listOf(basicItemOutput))) },
                { it, _ -> checkIfRecipeExists(it, name, getCookbookIfOneExists(it)) }
        )
    }

    @Order(6)
    @Test
    fun getsRecipes () {
        var engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        engine.listRecipes()
    }

    @Order(7)
    @Test
    fun updatesRecipe () {
        basicTxTestFlow(
                { it.updateRecipe(getRecipeIfOneExists(it), Core.userProfile!!.credentials.address, "wood!!!!!!!", getCookbookIfOneExists(it),
                        "fooBar description blahhhh", 0, listOf(), listOf(), WeightedParamList(listOf(), listOf())) },
                { it, _ -> checkIfRecipeExists(it, "wood!!!!!!!", getCookbookIfOneExists(it)) }
        )
    }

    @Order(8)
    @Test
    fun disablesRecipe () {
        basicTxTestFlow { it.disableRecipe(getRecipeIfOneExists(it)) }
    }

    @Order(9)
    @Test
    fun enablesRecipe () {
        basicTxTestFlow { it.enableRecipe(getRecipeIfOneExists(it)) }
    }

    @Order(10)
    @Test
    fun executesRecipe () {
        basicTxTestFlow { it.applyRecipe(getRecipeIfOneExists(it), arrayOf()) }
    }

}