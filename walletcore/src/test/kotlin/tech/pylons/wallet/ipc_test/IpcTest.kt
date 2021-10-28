package tech.pylons.wallet.ipc_test

import tech.pylons.wallet.core.Core
import tech.pylons.wallet.core.engine.TxPylonsDevEngine
import tech.pylons.wallet.core.engine.crypto.CryptoCosmos
import tech.pylons.lib.types.*
import tech.pylons.ipc.Message
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.wallet.core.internal.HttpWire
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@ExperimentalUnsignedTypes
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class IpcTest {
    val core = Core(Config(Backend.MANUAL, "pylonschain",true, listOf("http://127.0.0.1:1317"))).use()

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

//    @Test
//    fun handleBatchCreateRecipe () {
//        engineSetup()
//        val weightedOutputs = listOf(tech.pylons.lib.klaxon.toJsonString(listOf(tech.pylons.lib.klaxon.toJsonString(WeightedOutput(listOf(), "")))))
//        val coinInputs = listOf(tech.pylons.lib.klaxon.toJsonString(listOf(CoinInput("foo", 0))))
//        val itemInputs = listOf(tech.pylons.lib.klaxon.toJsonString(listOf(ItemInput("bar", ConditionList(listOf(), listOf(), listOf()), listOf(), listOf(), listOf(), FeeInputParam(0, 0)))))
//        Message.CreateRecipes(
//            creators = listOf("creator"),
//            cookbooks = listOf("Cookbook"),
//            ids = listOf("id"),
//            names = listOf("name"),
//            versions = listOf("ver"),
//            descriptions = listOf("descr"),
//            coinInputs = coinInputs,
//            itemInputs = itemInputs,
//            outputTables =  listOf(tech.pylons.lib.klaxon.toJsonString(EntriesList(listOf(), listOf(), listOf()))),
//            outputs =  weightedOutputs,
//            blockIntervals = listOf(0),
//            enabled = listOf(true),
//            extraInfos = listOf("")).resolve()
//        // this obv. won't work atm but i just wanna see how it deserializes
//    }
}