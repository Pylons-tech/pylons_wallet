package com.pylons.wallet.walletcore_test

import com.pylons.lib.PubKeyUtil
import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.lib.types.*
import com.pylons.lib.types.tx.Coin
import com.pylons.lib.types.tx.item.Item
import com.pylons.lib.types.tx.recipe.*
import com.pylons.wallet.core.internal.HttpWire
import com.pylons.wallet.core.internal.InternalPrivKeyStore
import com.pylons.wallet.walletcore_test.fixtures.emitCreateRecipe
import com.pylons.wallet.walletcore_test.fixtures.emitCreateTrade
import com.pylons.wallet.walletcore_test.fixtures.emitUpdateRecipe
import org.spongycastle.util.encoders.Hex
import org.junit.jupiter.api.*
import java.time.Instant

import java.util.*

@ExperimentalUnsignedTypes
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
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
    }
}