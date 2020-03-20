package com.pylons.wallet.walletcore_test.engine

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.jsonTemplate.*
import org.opentest4j.AssertionFailedError
import org.apache.commons.lang3.StringUtils.*

internal class TxPylonsDevEngineSignables {
    private fun engineSetup (key : String? = null) : TxPylonsDevEngine {
        Core.start(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317")), "")
        val engine = Core.engine as TxPylonsDevEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        if (key != null) {
            UserData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to key)
            engine.cryptoHandler.importKeysFromUserData()
        }
        else engine.cryptoHandler.generateNewKeys()
        Core.userProfile = Profile(engine.generateCredentialsFromKeys(), mutableMapOf(), listOf())
        return engine
    }

    private fun basicSignableTestFlow (msgType : String, signableFun : (TxPylonsDevEngine) -> String) {
        val engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        println("getting profile state...")
        engine.getOwnBalances()
        println("getting txbuilder output...")
        val fixture = engine.queryTxBuilder(msgType)
        println("generating sign struct")
        val signable = baseSignTemplate(signableFun(engine), 0, 0)
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
    fun disablesRecipeSignable () {
        basicSignableTestFlow("disable_recipe") { disableRecipeSignTemplate(
                "id0001","cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337")
        }
    }

    @Test
    fun enablesRecipeSignable () {
        basicSignableTestFlow("enable_recipe") {
            enableRecipeSignTemplate(
                    "id0001","cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337")
        }
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
    fun createsCookbookSignable () {
        basicSignableTestFlow("create_cookbook") {
            createCookbookSignTemplate(
                    "","name", "SketchyCo", "this has to meet character limits lol", "1.0.0",
                    "example@example.com", 0, "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337", 50
            )
        }
    }

    @Test
    fun updatesCookbookSignable () {
        basicSignableTestFlow("update_cookbook") {
            updateCookbookSignTemplate("cookbook id", "SketchyCo", "this has to meet character limits lol",
                    "1.0.0", "example@example.com", "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337")
        }
    }

    @Test
    fun sendsPylonsSignable () {
        basicSignableTestFlow("send_pylons") {
            sendPylonsSignTemplate(5, "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
                    "cosmos13rkt5rzf4gz8dvmwxxxn2kqy6p94hkpgluh8dj")
        }
    }

    @Test
    fun executeRecipeSignable () {
        basicSignableTestFlow("execute_recipe") {
            executeRecipeSignTemplate("id0001", arrayOf("alpha", "beta", "gamma"), "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337")
        }
    }
}