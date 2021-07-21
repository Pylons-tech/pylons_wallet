package tech.pylons.wallet.walletcore_test.engine

import tech.pylons.lib.PubKeyUtil
import org.junit.jupiter.api.Assertions.*
import tech.pylons.wallet.core.Core
import tech.pylons.wallet.core.engine.TxPylonsDevEngine
import tech.pylons.wallet.core.engine.crypto.CryptoCosmos
import tech.pylons.lib.types.*
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.wallet.core.internal.HttpWire
import tech.pylons.wallet.core.internal.InternalPrivKeyStore
import tech.pylons.wallet.walletcore_test.fixtures.emitCreateRecipe
import tech.pylons.wallet.walletcore_test.fixtures.emitCreateTrade
import tech.pylons.wallet.walletcore_test.fixtures.emitUpdateRecipe
import org.spongycastle.util.encoders.Hex
import org.junit.jupiter.api.*
import java.time.Instant

import java.util.*

@ExperimentalUnsignedTypes
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TxPylonsDevEngineOnline {
    val core = Core(Config(Backend.MANUAL, "pylonschain",true, listOf("http://127.0.0.1:1317"))).use()

    companion object {
        var exportedKey : String? = null
        var exportedRecipeName : String? = null
        var exportedCookbook : String? = null
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

    private fun getActiveTradeIfOneExists (engine: TxPylonsDevEngine) : String {
        val t = engine.listTrades().filter { !it.completed && !it.disabled }
        return when (t.isNotEmpty()) {
            true -> t[t.lastIndex].id
            false -> fail("No active trades exist on chain. This test cannot continue.")
        }
    }

    private fun getItemIfOneExists (engine: TxPylonsDevEngine) : Item {
        val r = engine.getMyProfileState()!!.items
        return when (r.isNotEmpty()) {
            true -> r[r.lastIndex]
            false -> fail("No items exist on chain belonging to current address. This test cannot continue.")
        }
    }

    private fun engineSetup (key : String? = null) : TxPylonsDevEngine {
        HttpWire.verbose = true
        core.start("")
        val engine = core.engine as TxPylonsDevEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        if (key != null) {
            println("Key is not null")
            core.userData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to key)
            engine.cryptoHandler.importKeysFromUserData()
        }
        else engine.cryptoHandler.generateNewKeys()
        core.userProfile = MyProfile.getDefault(core)
        if (key != null) assertEquals(key, Hex.toHexString(engine.cryptoHandler.keyPair?.secretKey()?.bytesArray()))
        return engine
    }

    private fun checkIfRecipeExists (engine: TxPylonsDevEngine, recipeName: String, cookbook : String) {
        val recipes = engine.listRecipes()
        var recipe : Recipe? = null
        for (it : Recipe in recipes) { if (it.name == recipeName  && it.cookbookId == cookbook) { recipe = it; break } }
        assertNotNull(recipe, "could not find recipe $recipeName in cookbook $cookbook")
    }

    private fun basicTxTestFlow (txFun : (TxPylonsDevEngine) -> Transaction) = basicTxTestFlow(txFun, null)

    private fun basicTxTestFlow (txFun : (TxPylonsDevEngine) -> Transaction, followUp : ((TxPylonsDevEngine, String) -> Unit)?) {
        val engine = engineSetup(exportedKey)
        println("Running TX integration test.\n" +
                "Chain ID: ${core.config.chainId}\n" +
                "Private key: $exportedKey\n" +
                "Public key: ${PubKeyUtil.getCompressedPubkey(engine.cryptoCosmos.keyPair!!.publicKey()!!).toHexString()}\n" +
                "Address: ${core.userProfile?.address}\n" +
                "NEXT: Updating status block...\n" +
                "============================\n" +
                "\n")
        core.updateStatusBlock()
        println("Updated status block successfully.\n" +
                "Block time: ${core.statusBlock.blockTime}\n" +
                "Height: ${core.statusBlock.height}\n" +
                "WalletCore version: ${core.statusBlock.walletCoreVersion}\n" +
                "NEXT: Getting profile state before initial TX build...\n" +
                "============================\n" +
                "\n")
        engine.getMyProfileState()
        println("Got profile state successfully.\n" +
                "Address: ${core.userProfile?.address}\n" +
                "Pylons: ${core.userProfile?.coin("pylon")}\n" +
                "Number of items: ${core.userProfile?.items?.size}\n" +
                "NEXT: Building transaction...\n" +
                "============================\n" +
                "\n")
        val tx = txFun(engine)
        println("Built transaction successfully.\n" +
                "State: ${tx.state}\n" +
                "Message: ${tx.txData.msg}\n" +
                "Code: ${tx.code}\n" +
                "Status: ${tx.txData.status}\n" +
                "Raw log: ${tx.raw_log}\n"+
                "Message size: ${tx.stdTx?.msg?.size}b\n" +
                "NEXT: Submitting transaction...\n" +
                "============================\n" +
                "\n")
        tx.submit()
        println("Submitted transaction successfully.\n" +
                "State: ${tx.state}\n" +
                "Message: ${tx.txData.msg}\n" +
                "Code: ${tx.code}\n" +
                "Status: ${tx.txData.status}\n" +
                "Raw log: ${tx.raw_log}\n"+
                "Message size: ${tx.stdTx?.msg?.size}b\n" +
                "NEXT: Waiting for transaction to resolve...\n" +
                "============================\n" +
                "\n")
        while (true) {
            if (tx.state == Transaction.State.TX_NOT_YET_SENT) Thread.sleep(5000)
            else break
        }
        println("\n\nWaiting 5 seconds to allow chain to catch up...\n\n")
        Thread.sleep(5000)
        println("\n\nTransaction should be committed on local chain now.\n\n")
        engine.getMyProfileState()
        println("Got profile state successfully after TX commit.\n" +
                "Address: ${core.userProfile?.address}\n" +
                "Pylons: ${core.userProfile?.coin("pylon")}\n" +
                "Number of items: ${core.userProfile?.items?.size}\n" +
                "NEXT: Building transaction...\n" +
                "============================\n" +
                "\n")
        println("Final local transaction data check.\n" +
                "Hash: ${tx.id}\n" +
                "State: ${tx.state}\n" +
                "Message: ${tx.txData.msg}\n" +
                "Code: ${tx.code}\n" +
                "Status: ${tx.txData.status}\n" +
                "Raw log: ${tx.raw_log}\n"+
                "Message size: ${tx.stdTx?.msg?.size}b\n" +
                "NEXT: Retrieving accepted transaction from chain...\n" +
                "============================\n" +
                "\n")
        assertEquals(Transaction.State.TX_ACCEPTED, tx.state)

        val a = engine.getTransaction(tx.id!!)
        println("Retrieved transaction data check.\n" +
                "Hash: ${a.id}\n" +
                "State: ${a.state}\n" +
                "Message: ${a.txData.msg}\n" +
                "Code: ${a.code}\n" +
                "Status: ${a.txData.status}\n" +
                "Raw log: ${a.raw_log}\n"+
                "Message size: ${a.stdTx?.msg?.size}b\n" +
                "============================\n" +
                "\n")
        assertEquals(Transaction.ResponseCode.OK, a.code, "Response code of ${a.code}, not ResponseCode.OK")
        followUp?.invoke(engine, tx.id!!)
    }



    @Order(0)
    @Test
    fun createAccount () {
        basicTxTestFlow (
                { it.registerNewProfile("fuckio", null) },
                { it, _ -> exportedKey =
                        Hex.toHexString(it.cryptoCosmos.keyPair!!.secretKey().bytesArray())}
        )
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
        val id = "${kotlin.random.Random.nextInt()}"
        exportedCookbook = id
        val name = "blyyah ${Random().nextInt()}"
        basicTxTestFlow { it.createCookbook(id, name, "tst",
                "this is a description for a test flow cookbook i guess",
                "1.0.0", "fake@example.com", 0, 50) }
    }

    @Order(4)
    @Test
    fun getsCookbooks () {
        val engine = engineSetup(exportedKey)
        val a = engine.listCookbooks()
        assert(a.isNotEmpty())
    }

    @Order(5)
    @Test
    fun updatesCookbook () {
        val cb = exportedCookbook ?: throw Exception("exportedCookbook should not be null")
        basicTxTestFlow { it.updateCookbook(cb, "tst",
                "this is a description for updatescookbook test", "1.0.0", "example@example.com") }
    }

    @Order(6)
    @Test
    fun createsRecipe () {
        val name = "RTEST_${Instant.now().epochSecond}"
        val cb = exportedCookbook ?: throw Exception("exportedCookbook should not be null")
        exportedRecipeName = name
        basicTxTestFlow(
                { emitCreateRecipe(it, name,
                        cb, core.userProfile!!.credentials.address)},
                { it, _ -> checkIfRecipeExists(it, name, cb) }
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
        val name = exportedRecipeName ?: throw Exception("exportedRecipe should not be null")
        val cb = exportedCookbook ?: throw Exception("exportedCookbook should not be null")
        basicTxTestFlow(
                { emitUpdateRecipe(it, name, cb,
                        cb, core.userProfile!!.credentials.address)},
                { it, _ -> checkIfRecipeExists(it, name, cb) }
        )
    }

    @Order(10)
    @Test
    fun disablesRecipe () {
        val name = exportedRecipeName ?: throw Exception("exportedRecipe should not be null")
        basicTxTestFlow { it.disableRecipe(name) }
    }

    @Order(11)
    @Test
    fun enablesRecipe () {
        val name = exportedRecipeName ?: throw Exception("exportedRecipe should not be null")
        basicTxTestFlow { it.enableRecipe(name) }
    }

    @Order(12)
    @Test
    fun executesRecipe () {
        val name = exportedRecipeName ?: throw Exception("exportedRecipe should not be null")
        basicTxTestFlow { it.applyRecipe(name, listOf()) }
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
                { emitCreateTrade(it, getItemIfOneExists(it), core.userProfile!!.credentials.address)},
                { _, _ -> println("do trade chk later") }
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
                { emitCreateTrade(it, getItemIfOneExists(it), core.userProfile!!.credentials.address)},
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
            it.sendItems("cosmos1992pvmjlj3va7kcx8ldlrdgn0qkspvxe8snrk9", listOf(getItemIfOneExists(it).id)) }
    }

}