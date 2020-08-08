package com.pylons.wallet.walletcore_test.engine

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.ops.newProfile
import com.pylons.wallet.core.types.*
import org.apache.commons.codec.binary.Base64
import org.apache.tuweni.bytes.Bytes
import com.pylons.wallet.core.types.jsonTemplate.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import org.junit.jupiter.api.Test
import java.security.Security

@ExperimentalUnsignedTypes
class TxPylonsDevEngineOffline {
    private val compressedPubkey = "0391677BCE47D37E1DD4AB90F07B5C3209FC2761970ED839FCD7B5D351275AFC0B"

    private fun engineSetup (key : String? = null) : TxPylonsDevEngine {
        Security.addProvider(BouncyCastleProvider())
        Core.start(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317")), "")
        val engine = Core.engine as TxPylonsDevEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        if (key != null) {
            UserData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to key)
            engine.cryptoHandler.importKeysFromUserData()
        }
        else engine.cryptoHandler.generateNewKeys()
        Core.userProfile = MyProfile(engine.generateCredentialsFromKeys(), mutableMapOf(), listOf(), listOf())
        return engine
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

    @Test
    fun addressFromPubkey() {
        val engine = engineSetup(InternalPrivKeyStore.NODE_GENERATED_PRIVKEY)
        engine.getOwnBalances()
        val pubkey = engine.cryptoCosmos.keyPair!!.publicKey()
        val addr = CryptoCosmos.getAddressFromPubkey(pubkey)
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
        val signature = PylonsSECP256K1.sign(data, engine.cryptoCosmos.keyPair!!)
        println("signature : \n" + Hex.toHexString(signature.bytes().toArray().slice(0 until 64).toByteArray()))
        assertTrue(PylonsSECP256K1.verify(data, signature, engine.cryptoCosmos.keyPair!!.publicKey()))
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
        val json = getPylons(500, "DUMMYADDR", engine.cryptoCosmos.keyPair!!.publicKey(), 4, 0)
        assertEquals(fixture, json.trimIndent().replace(" ", ""))
    }
}