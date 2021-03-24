package com.pylons.wallet.walletcore_test

import com.pylons.lib.PubKeyUtil
import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.lib.types.*
import com.pylons.lib.types.tx.Coin
import com.pylons.lib.types.tx.msg.GetPylons
import com.pylons.wallet.core.internal.HttpWire
import com.pylons.wallet.core.internal.InternalPrivKeyStore
import org.spongycastle.util.encoders.Hex
import org.junit.jupiter.api.*

@ExperimentalUnsignedTypes
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
// This was used for regression testing in tracking down a particularly nasty
// signature bug. It's not really a test and it needs to be rewritten to actually
// log this info somewhere useful. TODO: rewrite this as a test that actually
// uses known-good fixtures to sanity check our output to ensure this never happens again.
class ExportVarious {
    val core = Core(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317"))).use()

    companion object {
        var exportedKey : String? = null
    }

    private fun engineSetup (key : String? = null) : TxPylonsDevEngine {
        HttpWire.verbose = true
        core.start("")
        val engine = core.engine as TxPylonsDevEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        if (key != null) {
            println("Key is not null")
            core.userData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to key)
            engine.cryptoHandler.importKeysFromUserData()
        }
        else engine.cryptoHandler.generateNewKeys()
        core.userProfile = MyProfile.getDefault(core)
        println(key)
        if (key != null) assertEquals(key, Hex.toHexString(engine.cryptoHandler.keyPair?.secretKey()?.bytesArray()))
        return engine
    }

    @Test
    fun export () {
        val engine = engineSetup(InternalPrivKeyStore.BANK_TEST_KEY)

        core.updateStatusBlock()
        println("privkey: ${engine.cryptoCosmos.keyPair!!.secretKey()!!.bytes().toHexString()}")
        println("pubkey: ${engine.cryptoCosmos.keyPair!!.publicKey()!!.bytes().toHexString()}")
        println("compressed pubkey: ${PubKeyUtil.getCompressedPubkey(engine.cryptoCosmos.keyPair!!.publicKey()!!).toHexString()}")
        println("status: ${core.statusBlock.toJson()}")

        val msg = GetPylons(listOf(Coin("pylon", 500)), core.userProfile!!.credentials.address)
        println("STRUCT")
        println(msg.toSignStruct())
        println("SIGNED")
        println(msg.toSignedTx())
    }
}