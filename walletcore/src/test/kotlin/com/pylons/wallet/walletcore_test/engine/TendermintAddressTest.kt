package com.pylons.wallet.walletcore_test.engine

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.types.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class TendermintAddressTest {
    val core = Core(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317"))).use()
    private val KNOWN_GOOD = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"

    @Test
    fun generatesAddress () {
        core.start("")
        val engine = core.engine as TxPylonsDevEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        core.userData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to InternalPrivKeyStore.BANK_TEST_KEY)
        engine.cryptoHandler.importKeysFromUserData()
        val generated = TxPylonsEngine.getAddressString(
                CryptoCosmos.getAddressFromKeyPair(engine.cryptoCosmos.keyPair!!).toArray())
        assertEquals(KNOWN_GOOD, generated,
                "Address mismatch w/ pubkey ${engine.cryptoCosmos.keyPair!!.publicKey().toHexString()} - " +
                        "expected: ${getDisplayString(KNOWN_GOOD)}\n" +
                        "got: ${getDisplayString(generated)}")
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