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
import org.bouncycastle.util.encoders.Hex
import java.sql.Time
import java.util.*

internal class TxPylonsDevTest {
    private val k_GaiaCli = "a96e62ed3955e65be32703f12d87b6b5cf26039ecfa948dc5107a495418e5330"
    private val k_Self = "7e5c0ad3c8771ffe29cff8752da55859fe787f9677003bf8f78b78c6b87ea486"
    private val k_Third = "0XmJ33XhHvQjTBv3eIItl307Q8AcDKxQo9iF2DA==yik8"
    private val k_CompressedPubkey = "0391677BCE47D37E1DD4AB90F07B5C3209FC2761970ED839FCD7B5D351275AFC0B"
    private val k_ApacheSecret = "c85ef7d79691fe79573b1a7064c19c1a9819ebdbd1faaab1a8ec92344438aaf4"
    private val k_Bullshit = "ddddf7d79691fe79573b1a7064c19c1a9819ebdbd1faaab1a8ec92344438aaf4"
    private val k_Jack = "c651abedcf4b636b556d868b6b85376ec97e1be26a050adb1f1f7a2fbc1c7776"

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
        val signable = TxJson.msgTemplate_Signable(signableFun(engine), 0, 0)
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
        val engine = engineSetup(k_GaiaCli)
        val json = TxJson.getPylons(500, "DUMMYADDR", engine.cryptoHandler.keyPair!!.publicKey(), 4, 0)
        assertEquals(fixture, json.trimIndent().replace(" ", ""))
    }

    @Test
    fun createRecipeSignable () {
        basicSignableTestFlow("create_recipe") { TxJson.msgTemplate_SignComponent_CreateRecipe(
                "name","id001", "this has to meet character limits lol", 0,
                TxJson.getInputOutputListForSigning(mapOf("Wood" to 5)), TxJson.getInputOutputListForSigning(mapOf("Chair" to 1)),
                Core.userProfile!!.credentials.address)
        }
    }

    @Test
    fun updateRecipeSignable () {
        basicSignableTestFlow("update_recipe") { TxJson.msgTemplate_SignComponent_UpdateRecipe(
                "recipeName", "name","id001", "this has to meet character limits lol", 0,
                TxJson.getInputOutputListForSigning(mapOf("Wood" to 5)), TxJson.getInputOutputListForSigning(mapOf("Chair" to 1)),
                Core.userProfile!!.credentials.address)
        }
    }

    @Test
    fun createsCookbookSignable () {
        basicSignableTestFlow("create_cookbook") {
            TxJson.msgTemplate_SignComponent_CreateCookbook(
                    "name", "SketchyCo", "this has to meet character limits lol", "1.0.0",
                    "example@example.com", 0, Core.userProfile!!.credentials.address
            )
        }
    }

    @Test
    fun updatesCookbookSignable () {
        basicSignableTestFlow("update_cookbook") {
            TxJson.msgTemplate_SignComponent_UpdateCookbook("cookbook id", "SketchyCo", "this has to meet character limits lol",
                    "1.0.0", "example@example.com", Core.userProfile!!.credentials.address)
        }
    }

    @Test
    fun sendsPylonsSignable () {
        basicSignableTestFlow("send_pylons") {
            TxJson.msgTemplate_SignComponent_SendPylons(5, Core.userProfile!!.credentials.address,
                    "cosmos13rkt5rzf4gz8dvmwxxxn2kqy6p94hkpgluh8dj")
        }
    }

    @Test
    fun createsRecipe () {
        basicTxTestFlow { it.createRecipe("name","blah 1200783309",
                "this is a test recipe description which must comply w/ character limits",
                mapOf("wood" to 5), mapOf("chair" to 1),
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
        basicTxTestFlow { it.updateRecipe("name","blah 1200783309", "",
                "this is a test recipe description which must comply w/ character limits",
                mapOf("wood" to 5), mapOf("chair" to 1),
                0) }
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
        val pubkey = CryptoCosmos.getUncompressedPubkey(Base64().decode("Avz04VhtKJh8ACCVzlI8aTosGy0ikFXKIVHQ3jKMrosH"))
        val addr = CryptoCosmos.getAddressFromPubkey(pubkey.bytes())
        assertEquals("cosmos1g9ahr6xhht5rmqven628nklxluzyv8z9jqjcmc", TxPylonsEngine.getAddressString(addr.toArray()))
    }

    @Test
    fun roundTripDecompressPubkey () {
        val pubkeyAsBytes = Hex.decode(k_CompressedPubkey)
        val decompressed = CryptoCosmos.getUncompressedPubkey(pubkeyAsBytes)
        val recompressed = CryptoCosmos.getCompressedPubkey(decompressed)
        println(Hex.toHexString(recompressed.toArray()))
        assertArrayEquals(pubkeyAsBytes, recompressed.toArray())
    }

    @Test
    fun signature () {
        val data = Bytes.wrap("This is an example of a signed message.".toByteArray(Charsets.UTF_8))
        println("signing: \n" + data.toHexString())
        val engine = engineSetup(k_ApacheSecret)
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