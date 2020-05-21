package com.pylons.wallet.walletcore_test.engine

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.types.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.apache.tuweni.bytes.Bytes32
import org.apache.tuweni.crypto.SECP256K1
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex

class TendermintAddressTest {
    private val REPETITIONS = 100

    @Test
    fun generatesAddress () {
        HttpWire.verbose = true
        Core.start(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317")), "")
        val engine = Core.engine as TxPylonsDevEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        for (i in 0 until REPETITIONS) {
            engine.cryptoHandler.generateNewKeys()
            val nodeGenerated = TxPylonsEngine.getAddressFromNode(engine.cryptoCosmos.keyPair!!.publicKey())
            val selfGenerated = TxPylonsEngine.getAddressString(
                    CryptoCosmos.getAddressFromKeyPair(engine.cryptoCosmos.keyPair!!).toArray())
            assertEquals(nodeGenerated, selfGenerated,
                    "($i) Address mismatch w/ pubkey ${engine.cryptoCosmos.keyPair!!.publicKey().toHexString()} - " +
                            "expected: ${getDisplayString(nodeGenerated)}\n" +
                            "got: ${getDisplayString(selfGenerated)}")
        }
    }

    private fun getDisplayString (bech32 : String) : String {
        val b = Bech32Cosmos.decode(bech32)
        return "${b.hrp} $bech32   (${byteArrayToBinString(b.data)})"
    }

    private fun byteArrayToBinString(b : ByteArray) : String {
        val sb = StringBuilder()
        b.forEach { sb.append(" ${it.toString(2)} ") }
        return sb.toString()
    }
}