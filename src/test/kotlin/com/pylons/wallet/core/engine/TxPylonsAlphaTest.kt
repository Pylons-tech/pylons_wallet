package com.pylons.wallet.core.engine

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.types.*
import org.apache.commons.codec.binary.Base64
import org.apache.tuweni.bytes.Bytes
import com.pylons.wallet.core.types.SECP256K1
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.Security

internal class TxPylonsAlphaTest {
    private val k_GaiaCli = "a96e62ed3955e65be32703f12d87b6b5cf26039ecfa948dc5107a495418e5330"
    private val k_Self = "7e5c0ad3c8771ffe29cff8752da55859fe787f9677003bf8f78b78c6b87ea486"
    private val k_Third = "0XmJ33XhHvQjTBv3eIItl307Q8AcDKxQo9iF2DA==yik8"
    private val k_CompressedPubkey = "0391677BCE47D37E1DD4AB90F07B5C3209FC2761970ED839FCD7B5D351275AFC0B"
    private val k_ApacheSecret = "c85ef7d79691fe79573b1a7064c19c1a9819ebdbd1faaab1a8ec92344438aaf4"
    private val k_Jack = "c651abedcf4b636b556d868b6b85376ec97e1be26a050adb1f1f7a2fbc1c7776"

    @Test
    fun generateJson () {
        Core.start(Backend.ALPHA_REST, "")
        val engine = Core.engine as TxPylonsAlphaEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        //engine.cryptoHandler.generateNewKeys()
        UserData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to k_GaiaCli)

        engine.cryptoHandler.importKeysFromUserData()
        Core.userProfile = Profile(engine.generateCredentialsFromKeys(), mutableMapOf(), mutableMapOf(), mutableListOf())
        //val a = engine.getGetPylonsJson("500", "DUMMYADDR", engine.cryptoHandler.keyPair!!)
        val b = TxJson.getPylons(500, "DUMMYADDR", engine.cryptoHandler.keyPair!!.publicKey(), 4, 0)
        //assertEquals(a.trimIndent().replace("\\s".toRegex(), ""), b)
        // TODO: Rework this test now that the old functionality is kaput
    }

    @Test
    fun frankenstein() {
        Core.start(Backend.ALPHA_REST, "")
        val engine = Core.engine as TxPylonsAlphaEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        //engine.cryptoHandler.generateNewKeys()
        UserData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to k_Jack)

        engine.cryptoHandler.importKeysFromUserData()
        Core.userProfile = Profile(engine.generateCredentialsFromKeys(), mutableMapOf(), mutableMapOf(), mutableListOf())
        engine.getOwnBalances()
        engine.getPylons(500)
        //assertEquals(Transaction.State.TX_ACCEPTED, tx.state)
    }

    @Test
    fun addressFromPubkey() {
        val pubkey = CryptoCosmos.getUncompressedPubkey(Base64().decode("Avz04VhtKJh8ACCVzlI8aTosGy0ikFXKIVHQ3jKMrosH"))
        val addr = CryptoCosmos.getAddressFromPubkey(pubkey.bytes())
        assertEquals("cosmos1g9ahr6xhht5rmqven628nklxluzyv8z9jqjcmc", TxPylonsAlphaEngine.getAddressString(addr.toArray()))
    }

    @Test
    fun roundTripDecompressPubkey () {
        val pubkeyAsBytes = Hex.decode(k_CompressedPubkey)
        val decompressed = CryptoCosmos.getUncompressedPubkey(pubkeyAsBytes)
        val recompressed = CryptoCosmos.getCompressedPubkey(decompressed)
        System.out.println(Hex.toHexString(recompressed.toArray()))
        assertArrayEquals(pubkeyAsBytes, recompressed.toArray())
    }

    @Test
    fun signature () {
        val data = Bytes.wrap("This is an example of a signed message.".toByteArray(Charsets.UTF_8))
        println("signing: \n" + data.toHexString())
        Core.start(Backend.ALPHA_REST, "")
        val engine = Core.engine as TxPylonsAlphaEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        //engine.cryptoHandler.generateNewKeys()
        UserData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to k_ApacheSecret)

        engine.cryptoHandler.importKeysFromUserData()
        Core.userProfile = Profile(engine.generateCredentialsFromKeys(), mutableMapOf(), mutableMapOf(), mutableListOf())
        val signature = SECP256K1.sign(data, engine.cryptoHandler.keyPair)
        println("signature : \n" + Hex.toHexString(signature.bytes().toArray().slice(0 until 64).toByteArray()))
        val a = SECP256K1.verify(data, signature, engine.cryptoHandler.keyPair!!.publicKey())
        assertTrue(a)
        //assertEquals(Transaction.State.TX_ACCEPTED, tx.state)
    }

    @Test
    fun addressGen () {
        val key = CryptoCosmos.getUncompressedPubkey(Hex.decode("0283e197461d60d77d3b40e854646583ffebdcb12fa7f0327c4cd1c68b316e80f5"))
        val addr = CryptoCosmos.getAddressFromPubkey(key.bytes())
        assertArrayEquals(Hex.decode("050445E241606088942B8AE403DFF2FEF055CB0C"), addr.toArray())
    }

    @Test
    fun b64ToHex () {
        println(Hex.toHexString(Base64.decodeBase64("MEUCIQD02fsDPra8MtbRsyB1w7bqTM55Wu138zQbFcWx4+CFyAIge5WNPfKIuvzBZ69MyqHsqD8S1IwiEp+iUb6VSdtlpgY=")))
        println(Hex.toHexString(Base64.decodeBase64("vSfAvDzPMnJXy/wy5jMbTs6z+6KYe2CmWFEH3l2pQ21XJy1380CIcajBw34l5OOFZg03PdZ4O6ytuQH1SFU6vQ==")))

    }
}