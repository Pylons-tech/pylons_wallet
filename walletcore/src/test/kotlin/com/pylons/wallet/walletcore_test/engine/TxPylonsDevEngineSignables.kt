package com.pylons.wallet.walletcore_test.engine

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.types.*
import org.opentest4j.AssertionFailedError
import org.apache.commons.lang3.StringUtils.*
import java.io.StringReader

class TxPylonsDevEngineSignables {
    val core = Core().use()

    private fun engineSetup (key : String? = null) : TxPylonsDevEngine {
        core.start(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317")), "")
        val engine = core.engine as TxPylonsDevEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        if (key != null) {
            core.userData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to key)
            engine.cryptoHandler.importKeysFromUserData()
        }
        else engine.cryptoHandler.generateNewKeys()
        core.userProfile = MyProfile.getDefault(core)
        return engine
    }

    private fun basicSignableTestFlow (msgType : String, signableFun : (TxPylonsDevEngine) -> String) {
        val engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        println("getting profile state...")
        engine.getMyProfileState()
        println("getting txbuilder output...")
        var fixture = engine.queryTxBuilder(msgType)
        fixture = klaxon.parseJsonObject(StringReader(fixture)).obj("result")!!.toJsonString()
        println("generating sign struct")
        val signable = baseSignTemplate(signableFun(engine), 0, 0, 200000)
        println("generated: ${signableFun(engine)}")
        try {
            assertEquals(fixture, signable)
        } catch (e : AssertionFailedError) {
            println("-------------------\n" +
                    "DIFFERENCE: \n\n${difference(fixture, signable)}" +
                    "\n-------------------")
            throw e
        }
        println("FIXTURE\n$fixture\nGENERATED\n$signable")
        println("ok!")
    }

    @Test
    fun createAccountSignable () {
        val model = com.pylons.wallet.walletcore_test.fixtures.createAccountSignable
        basicSignableTestFlow("create_account") { model.toSignStruct() }
    }

    @Test
    fun getsPylonsSignable () {
        val model = com.pylons.wallet.walletcore_test.fixtures.getPylonsSignable
        basicSignableTestFlow("get_pylons") { model.toSignStruct() }
    }

    @Test
    fun googleIapGetsPylonsSignable () {
        val model = com.pylons.wallet.walletcore_test.fixtures.googleIapGetPylonsSignable
        basicSignableTestFlow("google_iap_get_pylons") { model.toSignStruct() }
    }

    @Test
    fun disableRecipeSignable () {
        val model = com.pylons.wallet.walletcore_test.fixtures.disableRecipeSignable
        basicSignableTestFlow("disable_recipe") { model.toSignStruct() }
    }

    @Test
    fun enableRecipeSignable () {
        val model = com.pylons.wallet.walletcore_test.fixtures.enableRecipeSignable
        basicSignableTestFlow("enable_recipe") { model.toSignStruct() }
    }

    @Test
    fun createRecipeSignable () {
        val model = com.pylons.wallet.walletcore_test.fixtures.createRecipeSignable
        basicSignableTestFlow("create_recipe") { model.toSignStruct() }
    }

    @Test
    fun updateRecipeSignable () {
        val model = com.pylons.wallet.walletcore_test.fixtures.updateRecipeSignable
        basicSignableTestFlow("update_recipe") { model.toSignStruct() }
    }

    @Test
    fun createCookbookSignable () {
        val model = com.pylons.wallet.walletcore_test.fixtures.createCookbookSignable
        basicSignableTestFlow("create_cookbook") { model.toSignStruct() }
    }

    @Test
    fun updateCookbookSignable () {
        val model = com.pylons.wallet.walletcore_test.fixtures.updateCookbookSignable
        basicSignableTestFlow("update_cookbook") { model.toSignStruct() }
    }

    @Test
    fun sendCoinsSignable () {
        val model = com.pylons.wallet.walletcore_test.fixtures.sendCoinsSignable
        basicSignableTestFlow("send_coins") { model.toSignStruct() }
    }

    @Test
    fun executeRecipeSignable () {
        val model = com.pylons.wallet.walletcore_test.fixtures.executeRecipeSignable
        basicSignableTestFlow("execute_recipe") { model.toSignStruct() }
    }
}