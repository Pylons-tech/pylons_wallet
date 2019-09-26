package com.pylons.wallet.core.engine

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.ops.newProfile
import com.pylons.wallet.core.types.*
import org.apache.commons.codec.binary.Base64
import org.apache.tuweni.bytes.Bytes
import com.pylons.wallet.core.types.SECP256K1
import com.pylons.wallet.core.types.item.Item
import com.pylons.wallet.core.types.item.prototype.*
import com.pylons.wallet.core.types.txJson.*
import com.squareup.moshi.Moshi
import org.bouncycastle.util.encoders.Hex
import org.junit.jupiter.api.MethodOrderer

import java.util.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TxPylonsDevTest {
    private val compressedPubkey = "0391677BCE47D37E1DD4AB90F07B5C3209FC2761970ED839FCD7B5D351275AFC0B"
    private val moshi = Moshi.Builder().build()

    private fun getRecipeTestId (engine: TxPylonsDevEngine) : String {
        return when (engine.url) {
            engine.MICHEAL_TEST_NODE_IP -> "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt33709e2bc6b-ac3c-4835-a8be-9d7a75b86f05"
            engine.GIRISH_TEST_NODE_IP -> "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337c59d30bc-ebf7-40d1-a322-3c727bfa580c"
            else -> ""
        }

    }

    private fun getCookbookIfOneExists (engine: TxPylonsDevEngine) : String? {
        val cb = engine.listCookbooks()
        return when (cb.isNotEmpty()) {
            true -> cb[0].id
            false -> null
        }
    }

    private fun engineSetup (key : String? = null) : TxPylonsDevEngine {
        Core.start(Backend.LIVE_DEV, "")
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
        println("ok!")
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
        println("submitting tx...")
        val tx = txFun(engine).submit()
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
    fun b64ToHex () {
        println(Hex.toHexString(Base64.decodeBase64("MEUCIQD02fsDPra8MtbRsyB1w7bqTM55Wu138zQbFcWx4+CFyAIge5WNPfKIuvzBZ69MyqHsqD8S1IwiEp+iUb6VSdtlpgY=")))
        println(Hex.toHexString(Base64.decodeBase64("vSfAvDzPMnJXy/wy5jMbTs6z+6KYe2CmWFEH3l2pQ21XJy1380CIcajBw34l5OOFZg03PdZ4O6ytuQH1SFU6vQ==")))
    }

    @Order(1)
    @Test
    fun dumpCredentials () {
        engineSetup()
        Core.newProfile("fucko")
        val str = Core.backupUserData()
        assertNotEquals("{}", str)
    }

    @Order(2)
    @Test
    fun disablesRecipeSignable () {
        basicSignableTestFlow("disable_recipe") { disableRecipeSignTemplate(
                "id0001","cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337")
        }
    }

    @Order(3)
    @Test
    fun enablesRecipeSignable () {
        basicSignableTestFlow("enable_recipe") {
            enableRecipeSignTemplate(
                    "id0001","cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337")
        }
    }

    @Order(4)
    @Test
    fun createRecipeSignable () {
        val prototype = ItemPrototype(mapOf("endurance" to DoubleParam(0.7, 1.0, 1.0, ParamType.INPUT_OUTPUT)),
                mapOf("HP" to LongParam(100, 140, 1.0, ParamType.INPUT_OUTPUT)),
                mapOf("Name" to StringParam("Raichu", 1.0, ParamType.INPUT_OUTPUT)))
        basicSignableTestFlow("create_recipe") { createRecipeSignTemplate(
                "name","id001", "this has to meet character limits lol", 0,
                getCoinIOListForSigning(mapOf("wood" to 5L)), getCoinIOListForSigning(mapOf("chair" to 1L)),
                getItemInputListForSigning(arrayOf(prototype)), getItemOutputListForSigning(arrayOf(prototype)),
                Core.userProfile!!.credentials.address)
        }
    }

    @Order(5)
    @Test
    fun updateRecipeSignable () {
        val prototype = ItemPrototype(mapOf("endurance" to DoubleParam(0.7, 1.0, 1.0, ParamType.INPUT_OUTPUT)),
                mapOf("HP" to LongParam(100, 140, 1.0, ParamType.INPUT_OUTPUT)),
                mapOf("Name" to StringParam("Raichu", 1.0, ParamType.INPUT_OUTPUT)))
        basicSignableTestFlow("update_recipe") { updateRecipeSignTemplate(
                "recipeName", "name","id001", "this has to meet character limits lol", 0,
                getCoinIOListForSigning(mapOf("Wood" to 5L)), getCoinIOListForSigning(mapOf("Chair" to 1L)),
                getItemInputListForSigning(arrayOf(prototype)), getItemOutputListForSigning(arrayOf(prototype)), Core.userProfile!!.credentials.address)
        }
    }

    @Order(6)
    @Test
    fun createsCookbookSignable () {
        basicSignableTestFlow("create_cookbook") {
            createCookbookSignTemplate(
                    "name", "SketchyCo", "this has to meet character limits lol", "1.0.0",
                    "example@example.com", 0, Core.userProfile!!.credentials.address
            )
        }
    }

    @Order(7)
    @Test
    fun updatesCookbookSignable () {
        basicSignableTestFlow("update_cookbook") {
            updateCookbookSignTemplate("cookbook id", "SketchyCo", "this has to meet character limits lol",
                    "1.0.0", "example@example.com", Core.userProfile!!.credentials.address)
        }
    }

    @Order(8)
    @Test
    fun sendsPylonsSignable () {
        basicSignableTestFlow("send_pylons") {
            sendPylonsSignTemplate(5, Core.userProfile!!.credentials.address,
                    "cosmos13rkt5rzf4gz8dvmwxxxn2kqy6p94hkpgluh8dj")
        }
    }

    @Order(9)
    @Test
    fun executeRecipeSignable () {
        basicSignableTestFlow("execute_recipe") {
            executeRecipeSignTemplate("id0001", arrayOf("alpha", "beta", "gamma"),"""cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337""")
        }
    }

    @Order(10)
    @Test
    fun addressFromPubkey() {
        val engine = engineSetup(InternalPrivKeyStore.NODE_GENERATED_PRIVKEY)
        engine.getOwnBalances()
        val pubkey = engine.cryptoHandler.keyPair!!.publicKey()
        val addr = CryptoCosmos.getAddressFromPubkey(pubkey.bytes())
        assertEquals(Core.userProfile!!.credentials.address, TxPylonsEngine.getAddressString(addr.toArray()))
    }

    @Order(11)
    @Test
    fun roundTripDecompressPubkey () {
        val pubkeyAsBytes = Hex.decode(compressedPubkey)
        val decompressed = CryptoCosmos.getUncompressedPubkey(pubkeyAsBytes)
        val recompressed = CryptoCosmos.getCompressedPubkey(decompressed)
        println(Hex.toHexString(recompressed.toArray()))
        assertArrayEquals(pubkeyAsBytes, recompressed.toArray())
    }

    @Order(12)
    @Test
    fun signature () {
        val data = Bytes.wrap("This is an example of a signed message.".toByteArray(Charsets.UTF_8))
        println("signing: \n" + data.toHexString())
        val engine = engineSetup(InternalPrivKeyStore.TUWENI_FIXTURES_SECRET)
        val signature = SECP256K1.sign(data, engine.cryptoHandler.keyPair)
        println("signature : \n" + Hex.toHexString(signature.bytes().toArray().slice(0 until 64).toByteArray()))
        assertTrue(SECP256K1.verify(data, signature, engine.cryptoHandler.keyPair!!.publicKey()))
    }

    @Order(13)
    @Test
    fun generateJson () {
        val fixture = """
            {
            "tx": {
                "msg": 
            [
            {
                "type": "pylons/GetPylons",
                "value": {
                "Amount": [
                {
                    "denom": "pylon",
                    "amount": "500"
                }
                ],
                "Requester": "DUMMYADDR"
            }
            }
            ]
        ,
    
                "fee": {
                "amount": null,
                "gas": "200000"
            },
                "signatures": [
                {
                    "pub_key": {
                    "type": "tendermint/PubKeySecp256k1",
                    "value": "ApUOHN/LEz1gJBCf1In3NO60UCQY5TjChIHyK84nbySM"
                },
                    "signature": "HD7lxC1Av2WkSljoY31LnV7VWFO9KxVDyGYNa7eUVUUyUPeI8J3Sw5rzFmM3vd2mLQuBK1o3AO/CrA37lpvLng=="
                }
                ],
                "memo": ""
            },
            "mode": "sync"
        }
        """.trimIndent().replace(" ", "")
        val engine = engineSetup(InternalPrivKeyStore.NODE_GENERATED_PRIVKEY)
        val json = getPylons(500, "DUMMYADDR", engine.cryptoHandler.keyPair!!.publicKey(), 4, 0)
        assertEquals(fixture, json.trimIndent().replace(" ", ""))
    }

    @Order(14)
    @Test
    fun getsPylons () {
        basicTxTestFlow { it.getPylons(500000) }
    }

    @Order(15)
    @Test
    fun sendsPylons () {
        basicTxTestFlow { it.sendPylons(1, "cosmos1hetxt4zc6kzq5ctepn9lz75jd5r4pkku0m5qch") }
    }

    @Order(16)
    @Test
    fun createsCookbook () {
        basicTxTestFlow { it.createCookbook("blyyah ${Random().nextInt()}", "tst",
                "this is a description for a test flow cookbook i guess",
                "1.0.0", "fake@example.com", 0) }
    }

    @Order(17)
    @Test
    fun getsCookbooks () {
        var engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        var a = engine.listCookbooks()
    }

    @Order(18)
    @Test
    fun updatesCookbook () {
        basicTxTestFlow { it.updateCookbook(getCookbookIfOneExists(it)!!, "tst",
                "this is a description for updatescookbook test", "1.0.0", "example@example.com") }
    }

    @Order(19)
    @Test
    fun createsRecipe () {
        val name = "wood ${Random().nextInt()}"
        basicTxTestFlow(
                { it.createRecipe(name, getCookbookIfOneExists(it)!!,
                            "this is a test recipe description which must comply w/ character limits",
                            mapOf("pylon" to 1L), mapOf("wood" to 1234567890L), arrayOf(), arrayOf(),
                            0) },
                { it, _ -> checkIfRecipeExists(it, name, getCookbookIfOneExists(it)!!) }
        )
    }

    @Order(20)
    @Test
    fun getsRecipes () {
        var engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        var a = engine.listRecipes()
    }

    @Order(21)
    @Test
    fun updatesRecipe () {
        basicTxTestFlow { it.updateRecipe("wood!!!!!!!", getCookbookIfOneExists(it)!!, getRecipeTestId(it),
                "behold, the wood economy. this is a recipe that outputs wood. it is very efficient.",
                mapOf("pylon" to 2L), mapOf("wood" to 1234567890L), arrayOf(), arrayOf(ItemPrototype()),
                0) }
    }

    @Order(22)
    @Test
    fun disablesRecipe () {
        basicTxTestFlow { it.disableRecipe(getRecipeTestId(it)) }
    }

    @Order(23)
    @Test
    fun enablesRecipe () {
        basicTxTestFlow { it.enableRecipe(getRecipeTestId(it)) }
    }

    @Order(24)
    @Test
    fun executesRecipe () {
        basicTxTestFlow { it.applyRecipe(getRecipeTestId(it), arrayOf()) }
    }

}