package com.pylons.wallet.core.engine

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.ops.newProfile
import com.pylons.wallet.core.types.*
import org.apache.commons.codec.binary.Base64
import org.apache.tuweni.bytes.Bytes
import com.pylons.wallet.core.types.SECP256K1
import com.pylons.wallet.core.types.txJson.*
import org.bouncycastle.util.encoders.Hex
import java.util.*

internal class TxPylonsDevTest {
    private val compressedPubkey = "0391677BCE47D37E1DD4AB90F07B5C3209FC2761970ED839FCD7B5D351275AFC0B"

    private fun getRecipeTestId (engine: TxPylonsDevEngine) : String {
        return when (engine.url) {
            engine.MICHEAL_TEST_NODE_IP -> "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt33709e2bc6b-ac3c-4835-a8be-9d7a75b86f05"
            engine.GIRISH_TEST_NODE_IP -> "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337c59d30bc-ebf7-40d1-a322-3c727bfa580c"
            else -> ""
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
        engine.getOwnBalances()
        val fixture = engine.queryTxBuilder(msgType)
        val signable = baseSignTemplate(signableFun(engine), 0, 0)
        assertEquals(fixture, signable)
    }

    private fun basicTxTestFlow (txFun : (TxPylonsDevEngine) -> Transaction) {
        val engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        engine.getOwnBalances()
        var oldSequence = (Core.userProfile!!.credentials as TxPylonsEngine.Credentials).sequence
        val tx = txFun(engine).submit()
        println("Waiting 5 seconds to allow chain to catch up")
        Thread.sleep(5000)
        engine.getOwnBalances()
        assertTrue((Core.userProfile!!.credentials as TxPylonsEngine.Credentials).sequence > oldSequence)
        assertEquals(Transaction.State.TX_ACCEPTED, tx.state)
    }

    @Test
    fun getsRecipes () {
        var engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)
        var a = engine.listRecipes("cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337")
        a.forEach { println("${it.id}, ${it.coinInputs.size}")}
    }

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
        basicSignableTestFlow("create_recipe") { createRecipeSignTemplate(
                "name","id001", "this has to meet character limits lol", 0,
                getCoinIOListForSigning(mapOf("Wood" to 5L)), getCoinIOListForSigning(mapOf("Chair" to 1L)),
                getItemInputListForSigning(arrayOf(Item("cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337e4acb5aa-e7cc-4b39-8118-798b493a6c61",
                        mapOf("Name" to "Pickachu"), mapOf("HP" to 100L), mapOf("endurance" to 0.75), "id001",
                        "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"))), "", Core.userProfile!!.credentials.address)
        }
    }

    @Test
    fun updateRecipeSignable () {
        basicSignableTestFlow("update_recipe") { updateRecipeSignTemplate(
                "recipeName", "name","id001", "this has to meet character limits lol", 0,
                getCoinIOListForSigning(mapOf("Wood" to 5L)), getCoinIOListForSigning(mapOf("Chair" to 1L)),
                "", "", Core.userProfile!!.credentials.address)
        }
    }

    @Test
    fun createsCookbookSignable () {
        basicSignableTestFlow("create_cookbook") {
            createCookbookSignTemplate(
                    "name", "SketchyCo", "this has to meet character limits lol", "1.0.0",
                    "example@example.com", 0, Core.userProfile!!.credentials.address
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
            executeRecipeSignTemplate("id0001","""cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337""")
        }
    }

    @Test
    fun executesRecipe () {
        basicTxTestFlow { it.applyRecipe(getRecipeTestId(it)) }
    }

    @Test
    fun createsRecipe () {
        basicTxTestFlow { it.createRecipe("more wood ${Random().nextInt()}","blah 1200783309",
                "this is a test recipe description which must comply w/ character limits",
                mapOf("pylon" to 1L), mapOf("wood" to 1234567890L), arrayOf(), arrayOf(),
                0) }
    }

    @Test
    fun createsCookbook () {
        basicTxTestFlow { it.createCookbook("blah ${Random().nextInt()}", "tst",
                "this is a description for a test flow cookbook i guess",
                "1.0.0", "fake@example.com", 0) }
    }

    @Test
    fun updatesCookbook () {
        basicTxTestFlow { it.updateCookbook("blah 1200783309", "tst",
                "this is a description for updatescookbook test", "1.0.0", "example@example.com") }
    }

    @Test
    fun updatesRecipe () {
        basicTxTestFlow { it.updateRecipe("wood!!!!!!!","blah 1200783309", getRecipeTestId(it),
                "behold, the wood economy. this is a recipe that outputs wood. it is very efficient.",
                mapOf("pylon" to 2L), mapOf("wood" to 1234567890L), arrayOf(), arrayOf(Item("", mapOf("type" to "widget"), mapOf(),
                mapOf(), "", "")),
                0) }
    }

    @Test
    fun disablesRecipe () {
        basicTxTestFlow { it.disableRecipe(getRecipeTestId(it)) }
    }

    @Test
    fun enablesRecipe () {
        basicTxTestFlow { it.enableRecipe(getRecipeTestId(it)) }
    }

    @Test
    fun getsPylons () {
        basicTxTestFlow { it.getPylons(500) }
    }

    @Test
    fun sendsPylons () {
        basicTxTestFlow { it.sendPylons(1, "cosmos1hetxt4zc6kzq5ctepn9lz75jd5r4pkku0m5qch") }
    }

    @Test
    fun addressFromPubkey() {
        val engine = engineSetup(InternalPrivKeyStore.NODE_GENERATED_PRIVKEY)
        engine.getOwnBalances()
        val pubkey = engine.cryptoHandler.keyPair!!.publicKey()
        val addr = CryptoCosmos.getAddressFromPubkey(pubkey.bytes())
        assertEquals(Core.userProfile!!.credentials.address, TxPylonsEngine.getAddressString(addr.toArray()))
    }

    @Test
    fun roundTripDecompressPubkey () {
        val pubkeyAsBytes = Hex.decode(compressedPubkey)
        val decompressed = CryptoCosmos.getUncompressedPubkey(pubkeyAsBytes)
        val recompressed = CryptoCosmos.getCompressedPubkey(decompressed)
        println(Hex.toHexString(recompressed.toArray()))
        assertArrayEquals(pubkeyAsBytes, recompressed.toArray())
    }

    @Test
    fun signature () {
        val data = Bytes.wrap("This is an example of a signed message.".toByteArray(Charsets.UTF_8))
        println("signing: \n" + data.toHexString())
        val engine = engineSetup(InternalPrivKeyStore.TUWENI_FIXTURES_SECRET)
        val signature = SECP256K1.sign(data, engine.cryptoHandler.keyPair)
        println("signature : \n" + Hex.toHexString(signature.bytes().toArray().slice(0 until 64).toByteArray()))
        assertTrue(SECP256K1.verify(data, signature, engine.cryptoHandler.keyPair!!.publicKey()))
    }

    @Test
    fun b64ToHex () {
        println(Hex.toHexString(Base64.decodeBase64("MEUCIQD02fsDPra8MtbRsyB1w7bqTM55Wu138zQbFcWx4+CFyAIge5WNPfKIuvzBZ69MyqHsqD8S1IwiEp+iUb6VSdtlpgY=")))
        println(Hex.toHexString(Base64.decodeBase64("vSfAvDzPMnJXy/wy5jMbTs6z+6KYe2CmWFEH3l2pQ21XJy1380CIcajBw34l5OOFZg03PdZ4O6ytuQH1SFU6vQ==")))
    }

    @Test
    fun dumpCredentials () {
        engineSetup()
        Core.newProfile("fucko")
        val str = Core.backupUserData()
        assertNotEquals("{}", str)
    }
}