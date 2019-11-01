package com.pylons.wallet.core.engine

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.item.prototype.*
import com.pylons.wallet.core.types.item.prototype.DoubleParam
import com.pylons.wallet.core.types.item.prototype.LongParam
import com.pylons.wallet.core.types.item.prototype.StringParam
import com.pylons.wallet.core.types.jsonModel.*
import com.pylons.wallet.core.types.jsonTemplate.*
import com.pylons.wallet.core.types.jsonModel.DoubleParam as mDParam
import com.pylons.wallet.core.types.jsonModel.LongParam as mLParam
import com.pylons.wallet.core.types.jsonModel.StringParam as mSParam

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
        Core.userProfile = Profile(engine.generateCredentialsFromKeys(), mutableMapOf(), mutableMapOf(), mutableListOf())
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
        assertEquals(fixture, signable)
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
        val model = CreateRecipe(
                blockInterval = 0,
                coinInputs = listOf(
                        CoinInput("wood", 5)
                ),
                cookbookId = "id001",
                description = "this has to meet character limits lol",
                entries = WeightedParamList(
                        coinOutputs = listOf(
                                CoinOutput("chair", 1, 1)
                        ),
                        itemOutputs = listOf(
                                ItemOutput(
                                        doubles = listOf(mDParam("1.0", "endurance",
                                                listOf(
                                                        DoubleWeightRange("500.00", "100.00", 6),
                                                        DoubleWeightRange("800.00","501.00", 2)
                                                )
                                        )
                                        ),
                                        longs = listOf(
                                                mLParam("", "HP",
                                                        listOf(
                                                                LongWeightRange(500, 100, 6),
                                                                LongWeightRange(800, 501, 2)
                                                        )
                                                )
                                        ),
                                        strings = listOf(
                                                mSParam("1.0", "Name", "Raichu")
                                        ),
                                        weight = 1
                                )
                        )
                ),
                itemInputs = listOf(
                    ItemInput(
                            doubles = listOf(
                                    DoubleInputParam("endurance", "100.00", "500.00")
                            ),
                            longs = listOf(
                                    LongInputParam("HP", 100, 500)
                            ),
                            strings = listOf(
                                    StringInputParam("Name", "Raichu")
                            )
                    )
                ),
                name = "name",
                sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"
        )
        basicSignableTestFlow("create_recipe") { model.toSignStruct() }
    }

    @Test
    fun updateRecipeSignable () {
        fail<Unit>("update me!")
//        val prototype = ItemPrototype(mapOf("endurance" to DoubleParam(0.7, 1.0, 1.0, ParamType.INPUT_OUTPUT)),
//                mapOf("HP" to LongParam(listOf(LongParam.WeightRange(100, 500, 6),
//                        LongParam.WeightRange(501, 800, 2)), 1.0, ParamType.INPUT_OUTPUT)),
//                mapOf("Name" to StringParam("Raichu", 1.0, ParamType.INPUT_OUTPUT)))
//        basicSignableTestFlow("update_recipe") { updateRecipeSignTemplate(
//                "recipeName", "name","id001", "this has to meet character limits lol", 0,
//                getCoinIOListForSigning(mapOf("wood" to 5L)), getCoinIOListForSigning(mapOf("chair" to 1L)),
//                getItemInputListForSigning(arrayOf(prototype)), getItemOutputListForSigning(arrayOf(prototype)), Core.userProfile!!.credentials.address)
//        }
    }

    @Test
    fun createsCookbookSignable () {
        basicSignableTestFlow("create_cookbook") {
            createCookbookSignTemplate(
                    "name", "SketchyCo", "this has to meet character limits lol", "1.0.0",
                    "example@example.com", 0, Core.userProfile!!.credentials.address, 50
            )
        }
    }

    @Test
    fun updatesCookbookSignable () {
        basicSignableTestFlow("update_cookbook") {
            updateCookbookSignTemplate("cookbook id", "SketchyCo", "this has to meet character limits lol",
                    "1.0.0", "example@example.com", Core.userProfile!!.credentials.address)
        }
    }

    @Test
    fun sendsPylonsSignable () {
        basicSignableTestFlow("send_pylons") {
            sendPylonsSignTemplate(5, Core.userProfile!!.credentials.address,
                    "cosmos13rkt5rzf4gz8dvmwxxxn2kqy6p94hkpgluh8dj")
        }
    }

    @Test
    fun executeRecipeSignable () {
        basicSignableTestFlow("execute_recipe") {
            executeRecipeSignTemplate("id0001", arrayOf("alpha", "beta", "gamma"),"""cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337""")
        }
    }
}