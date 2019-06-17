package com.pylons.wallet.core.engine

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.types.*
import org.apache.commons.codec.binary.Base64
import org.apache.tuweni.bytes.Bytes
import org.apache.tuweni.crypto.SECP256K1
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import java.security.Security

internal class TxPylonsAlphaTest {
    private val k_GaiaCli = "a96e62ed3955e65be32703f12d87b6b5cf26039ecfa948dc5107a495418e5330"
    private val k_Self = "7e5c0ad3c8771ffe29cff8752da55859fe787f9677003bf8f78b78c6b87ea486"
    private val k_Third = "0XmJ33XhHvQjTBv3eIItl307Q8AcDKxQo9iF2DA==yik8"
    private val k_CompressedPubkey = "0291677BCE47D37E1DD4AB90F07B5C3209FC2761970ED839FCD7B5D351275AFC0B"

    @Test
    fun frankenstein() {
        Security.addProvider(BouncyCastleProvider())
        Core.start(Backend.ALPHA_REST, "")
        val engine = Core.engine as TxPylonsAlphaEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        //engine.cryptoHandler.generateNewKeys()
        UserData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to k_GaiaCli)

        engine.cryptoHandler.importKeysFromUserData()
        Core.userProfile = Profile(engine.getNewCredentials(), mutableMapOf(), mutableMapOf(), mutableListOf())
        engine.getPylons(501)
        //assertEquals(Transaction.State.TX_ACCEPTED, tx.state)
    }

    @Test
    fun addressFromPubkey() {
        val pubkey =
        SECP256K1.PublicKey.fromBytes(Bytes.wrap(Base64().decode("Avz04VhtKJh8ACCVzlI8aTosGy0ikFXKIVHQ3jKMrosH"))).bytesArray()
        val addr = CryptoCosmos.getAddressFromPubkey(Bytes.wrap(pubkey))
        assertEquals("cosmos1g9ahr6xhht5rmqven628nklxluzyv8z9jqjcmc", TxPylonsAlphaEngine.getAddressString(addr.toArray()))
    }

    @Test
    fun bech32Pubkey () {
        Security.addProvider(BouncyCastleProvider())
        Core.start(Backend.ALPHA_REST, "")
        val engine = Core.engine as TxPylonsAlphaEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        UserData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to Hex.toHexString(Base64.decodeBase64(k_Third)))
        engine.cryptoHandler.importKeysFromUserData()
        var pubkey = engine.cryptoHandler.keyPair!!.publicKey()
        System.out.println( Bech32Cosmos.convertAndEncode("cosmospub", pubkey.bytesArray()))
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
    fun addressGen () {
        val key = CryptoCosmos.getUncompressedPubkey(Hex.decode("0283e197461d60d77d3b40e854646583ffebdcb12fa7f0327c4cd1c68b316e80f5"))
        val addr = CryptoCosmos.getAddressFromPubkey(key.bytes())
        assertArrayEquals(Hex.decode("050445E241606088942B8AE403DFF2FEF055CB0C"), addr.toArray())
    }
}