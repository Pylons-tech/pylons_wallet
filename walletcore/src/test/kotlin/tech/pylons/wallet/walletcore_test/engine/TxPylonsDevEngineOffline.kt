package tech.pylons.wallet.walletcore_test.engine

import tech.pylons.lib.PubKeyUtil
import org.junit.jupiter.api.Assertions.*
import tech.pylons.wallet.core.Core
import tech.pylons.wallet.core.engine.TxPylonsDevEngine
import tech.pylons.wallet.core.engine.TxPylonsEngine
import tech.pylons.wallet.core.engine.crypto.CryptoCosmos
import tech.pylons.lib.types.*
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.msg.GetPylons
import tech.pylons.wallet.core.internal.InternalPrivKeyStore
import org.apache.commons.codec.binary.Base64
import org.apache.tuweni.bytes.Bytes
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.spongycastle.util.encoders.Hex
import org.junit.jupiter.api.Test
import java.security.Security

@ExperimentalUnsignedTypes
class TxPylonsDevEngineOffline {
    val core = Core(Config(Backend.MANUAL, "pylonschain",true, listOf("http://127.0.0.1:1317"))).use()

    private val compressedPubkey = "0391677BCE47D37E1DD4AB90F07B5C3209FC2761970ED839FCD7B5D351275AFC0B"

    private fun engineSetup (key : String? = null) : TxPylonsDevEngine {
        Security.addProvider(BouncyCastleProvider())
        core.start("")
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

    @Test
    fun b64ToHex () {
        println(Hex.toHexString(Base64.decodeBase64("MEUCIQD02fsDPra8MtbRsyB1w7bqTM55Wu138zQbFcWx4+CFyAIge5WNPfKIuvzBZ69MyqHsqD8S1IwiEp+iUb6VSdtlpgY=")))
        println(Hex.toHexString(Base64.decodeBase64("vSfAvDzPMnJXy/wy5jMbTs6z+6KYe2CmWFEH3l2pQ21XJy1380CIcajBw34l5OOFZg03PdZ4O6ytuQH1SFU6vQ==")))
    }

    @Test
    fun dumpCredentials () {
        engineSetup()
        core.newProfile("fucko")
        val str = core.backupUserData()
        assertNotEquals("{}", str)
    }

    @Test
    fun addressFromPubkey() {
        val engine = engineSetup(InternalPrivKeyStore.NODE_GENERATED_PRIVKEY)
        engine.getMyProfileState()
        val pubkey = engine.cryptoCosmos.keyPair!!.publicKey()
        val addr = PubKeyUtil.getAddressFromPubkey(pubkey)
        assertEquals(core.userProfile!!.credentials.address, TxPylonsEngine.getAddressString(addr.toArray()))
    }

    @Test
    fun roundTripDecompressPubkey () {
        val pubkeyAsBytes = Hex.decode(compressedPubkey)
        val decompressed = PubKeyUtil.getUncompressedPubkey(pubkeyAsBytes)
        val recompressed = PubKeyUtil.getCompressedPubkey(decompressed)
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
        "tx_bytes":"ClcKVQoUL3B5bG9ucy5Nc2dHZXRQeWxvbnMSPQoMCgVweWxvbhIDNTAwEi1jb3Ntb3MxMHM0bWcyNXR1NnRlcm1yazhlZ2x0ZnltZTRxN3NnM2hlcjIzOXUSVgpOCkYKHy9jb3Ntb3MuY3J5cHRvLnNlY3AyNTZrMS5QdWJLZXkSIwohApUOHN/LEz1gJBCf1In3NO60UCQY5TjChIHyK84nbySMEgQKAggBEgQQgLUYGkDPrSipdcX5PGCkZZ2epLi1YNDL/fR5uWgUldPnoNGxWUOm4Do9gyfWwfHhgg8yDVZjsnxp7ypUmLlC9N5p5mO0",
        "mode":"BROADCAST_MODE_BLOCK"
        }
        """.trimIndent().replace(" ", "")
        engineSetup(InternalPrivKeyStore.NODE_GENERATED_PRIVKEY)
        val json = GetPylons(listOf(Coin("pylon", 500)), core.userProfile!!.address).toSignedTx()
        assertEquals(fixture, json.trimIndent().replace(" ", ""))
    }
}