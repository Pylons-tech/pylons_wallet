package com.pylons.wallet.ipc_test

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.lib.types.*
import com.pylons.ipc.Message
import com.pylons.lib.core.ICore
import com.pylons.lib.types.tx.recipe.*
import com.pylons.wallet.core.internal.HttpWire
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@ExperimentalUnsignedTypes
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class IpcTest {
    val core = Core(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1"))).use()

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
        return engine
    }

    @Test
    fun handleBatchCreateRecipe () {
        val weightedOutputs = listOf(com.pylons.lib.klaxon.toJsonString(listOf(com.pylons.lib.klaxon.toJsonString(WeightedOutput(listOf(), "")))))
        val coinInputs = listOf(com.pylons.lib.klaxon.toJsonString(listOf(CoinInput("foo", 0))))
        val itemInputs = listOf(com.pylons.lib.klaxon.toJsonString(listOf(ItemInput("bar", ConditionList(listOf(), listOf(), listOf()), listOf(), listOf(), listOf(), FeeInputParam(0, 0)))))
        Message.CreateRecipes(listOf("name"), listOf("foo"), listOf("descr"),
        listOf(0), coinInputs, itemInputs,
        listOf(com.pylons.lib.klaxon.toJsonString(EntriesList(listOf(), listOf(), listOf()))),
        weightedOutputs).resolve()
        // this obv. won't work atm but i just wanna see how it deserializes
    }
}