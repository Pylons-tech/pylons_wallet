package com.pylons.wallet.walletcore_test.engine

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.tx.item.Item
import com.pylons.wallet.core.types.tx.recipe.*
import com.pylons.wallet.walletcore_test.fixtures.emitCreateRecipe
import com.pylons.wallet.walletcore_test.fixtures.emitCreateTrade
import com.pylons.wallet.walletcore_test.fixtures.emitUpdateRecipe
import org.junit.jupiter.api.MethodOrderer
import java.time.Instant

import java.util.*

@ExperimentalUnsignedTypes
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TxPylonsDevEngineOnline {
    private fun getCookbookIfOneExists (engine: TxPylonsDevEngine) : String {
        val cb = engine.listCookbooks()
        return when (cb.isNotEmpty()) {
            true -> cb[0].id
            false -> fail("No cookbooks exist on chain belonging to current address. This test cannot continue.")
        }
    }

    private fun getExecutionIfOneExists (engine: TxPylonsDevEngine) : String {
        val e = engine.getPendingExecutions().filter {
            !it.completed
        }
        return when (e.isNotEmpty()) {
            true -> e[e.lastIndex].id
            false -> fail("No executions exist on chain belonging to current address. This test cannot continue.")
        }
    }

    private fun getRecipeIfOneExists (engine: TxPylonsDevEngine) : String {
        val r = engine.listRecipes()
        return when (r.isNotEmpty()) {
            true -> r[r.lastIndex].id
            false -> fail("No recipes exist on chain belonging to current address. This test cannot continue.")
        }
    }

    private fun getActiveTradeIfOneExists (engine: TxPylonsDevEngine) : String {
        val t = engine.listTrades().filter { !it.completed && !it.disabled }
        return when (t.isNotEmpty()) {
            true -> t[t.lastIndex].id
            false -> fail("No active trades exist on chain. This test cannot continue.")
        }
    }

    private fun getItemIfOneExists (engine: TxPylonsDevEngine) : Item {
        val r = engine.getOwnBalances()!!.items
        return when (r.isNotEmpty()) {
            true -> r[r.lastIndex]
            false -> fail("No items exist on chain belonging to current address. This test cannot continue.")
        }
    }

    private fun engineSetup (key : String? = null) : TxPylonsDevEngine {
        HttpWire.verbose = true
        Core.start(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317")), "")
        val engine = Core.engine as TxPylonsDevEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        if (key != null) {
            println("Key is not null")
            UserData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to key)
            engine.cryptoHandler.importKeysFromUserData()
        }
        else engine.cryptoHandler.generateNewKeys()
        Core.userProfile = MyProfile.default
        return engine
    }

    private fun checkIfRecipeExists (engine: TxPylonsDevEngine, recipeName: String, cookbook : String) {
        val recipes = engine.listRecipes()
        var recipe : Recipe? = null
        for (it : Recipe in recipes) { if (it.name == recipeName  && it.cookbookId == cookbook) { recipe = it; break } }
        assertNotNull(recipe, "could not find recipe $recipeName in cookbook $cookbook")
        println(recipe?.name)
    }

    private fun basicTxTestFlow (txFun : (TxPylonsDevEngine) -> Transaction) = basicTxTestFlow(txFun, null)

    private fun basicTxTestFlow (txFun : (TxPylonsDevEngine) -> Transaction, followUp : ((TxPylonsDevEngine, String) -> Unit)?) {
        val engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        println("pubkey: ${CryptoCosmos.getCompressedPubkey(engine.cryptoCosmos.keyPair!!.publicKey()!!).toHexString()}")
        println("getting profile state...")
        engine.getOwnBalances()
        val oldSequence = (Core.userProfile!!.credentials as TxPylonsEngine.Credentials).sequence
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
        //This check is no longer used bc we have a cleaner way to make sure transactions are accepted,
        //but you may want to uncomment it if Extremely Weird things are happening when interacting w/ the chain
        //and you're trying to debug.
        //assertTrue((Core.userProfile!!.credentials as TxPylonsEngine.Credentials).sequence > oldSequence)

        println("txhash: ${tx.id}")
        assertEquals(Transaction.State.TX_ACCEPTED, tx.state)
        println("ok!")
        val a = engine.getTransaction(tx.id!!)
        println(a.stdTx!!.msg.size)

        assertEquals(Transaction.ResponseCode.OK, a.code, "Not OK Response Code")

        followUp?.invoke(engine, tx.id!!)
    }

    @Order(-1)
    @Test
    fun createsAccount () {
        basicTxTestFlow { it.registerNewProfile("fuckio", null) }
    }

    @Order(0)
    @Test
    fun createAccount () {
        //TODO: Will fail if account exists, handle fail or clear chain for testing?
        basicTxTestFlow { it.createChainAccount() }
    }

    @Order(1)
    @Test
    fun getsPylons () {
        basicTxTestFlow { it.getPylons(500000) }
    }

    @Order(2)
    @Test
    fun sendsCoins () {
        basicTxTestFlow { it.sendCoins(listOf(Coin("pylon", 1)),
                "cosmos1hetxt4zc6kzq5ctepn9lz75jd5r4pkku0m5qch") }
    }

    @Order(3)
    @Test
    fun createsCookbook () {
        basicTxTestFlow { it.createCookbook("${kotlin.random.Random.nextInt()}","blyyah ${Random().nextInt()}", "tst",
                "this is a description for a test flow cookbook i guess",
                "1.0.0", "fake@example.com", 0, 50) }
    }

    @Order(4)
    @Test
    fun getsCookbooks () {
        val engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        val a = engine.listCookbooks()
        assert(a.isNotEmpty())
    }

    @Order(5)
    @Test
    fun updatesCookbook () {
        basicTxTestFlow { it.updateCookbook(getCookbookIfOneExists(it), "tst",
                "this is a description for updatescookbook test", "1.0.0", "example@example.com") }
    }

    @Order(6)
    @Test
    fun createsRecipe () {
        val name = "RTEST_${Instant.now().epochSecond}"
        basicTxTestFlow(
                { emitCreateRecipe(it, name,
                        getCookbookIfOneExists(it), Core.userProfile!!.credentials.address)},
                { it, _ -> checkIfRecipeExists(it, name, getCookbookIfOneExists(it)) }
        )
    }

    @Order(7)
    @Test
    fun getsRecipes () {
        val engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        engine.listRecipes()
    }

    @Order(8)
    @Test
    fun getsExecutions () {
        val engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        engine.getPendingExecutions()
    }

    @Order(9)
    @Test
    fun updatesRecipe () {
        val name = "RTEST_${Instant.now().epochSecond}"
        basicTxTestFlow(
                { emitUpdateRecipe(it, name, getCookbookIfOneExists(it),
                        getRecipeIfOneExists(it), Core.userProfile!!.credentials.address)},
                { it, _ -> checkIfRecipeExists(it, name, getCookbookIfOneExists(it)) }
        )
    }

    @Order(10)
    @Test
    fun disablesRecipe () {
        basicTxTestFlow { it.disableRecipe(getRecipeIfOneExists(it)) }
    }

    @Order(11)
    @Test
    fun enablesRecipe () {
        basicTxTestFlow { it.enableRecipe(getRecipeIfOneExists(it)) }
    }

    @Order(12)
    @Test
    fun executesRecipe () {
        basicTxTestFlow { it.applyRecipe(getRecipeIfOneExists(it), arrayOf()) }
    }

    @Order(13)
    @Test
    fun checksExecution () {
        basicTxTestFlow { it.checkExecution(getExecutionIfOneExists(it), true) }
    }

    @Order(14)
    @Test
    fun createsTrade () {
        basicTxTestFlow(
                { emitCreateTrade(it, getItemIfOneExists(it), Core.userProfile!!.credentials.address)},
                { it, _ -> println("do trade chk later") }
        )
    }

    @Order(15)
    @Test
    fun getsTrades () {
        val engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        val a = engine.listTrades()
        assert(a.isNotEmpty())
    }

    @Order(16)
    @Test
    fun fulfillsTrade () {
        basicTxTestFlow { it.fulfillTrade(getActiveTradeIfOneExists(it), listOf()) }
    }

    @Order(17)
    @Test
    fun setsItemString () {
        basicTxTestFlow { it.setItemFieldString(getItemIfOneExists(it).id, "Name", "very") }
    }

    @Order(18)
    @Test
    fun createsTradeForCancel () {
        basicTxTestFlow(
                { emitCreateTrade(it, getItemIfOneExists(it), Core.userProfile!!.credentials.address)},
                { it, id -> println("do trade chk later") }
        )
    }

    @Order(19)
    @Test
    fun cancelsTrade () {
        basicTxTestFlow {
            it.cancelTrade(getActiveTradeIfOneExists(it)) }
    }

    @Order(20)
    @Test
    fun sendsItems () {
        basicTxTestFlow {
            it.sendItems(Core.userProfile!!.credentials.address, "cosmos1992pvmjlj3va7kcx8ldlrdgn0qkspvxe8snrk9", listOf(getItemIfOneExists(it).id)) }
    }

}