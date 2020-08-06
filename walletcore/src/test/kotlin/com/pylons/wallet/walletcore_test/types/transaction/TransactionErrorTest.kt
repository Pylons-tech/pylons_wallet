package com.pylons.wallet.walletcore_test.types.transaction

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.ops.getTransaction
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.tx.recipe.CoinInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class TransactionErrorTest {

    private val config = Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317"))
    private val key = InternalPrivKeyStore.BANK_TEST_KEY

    init {
        if (!Core.isReady()) {
            Core.start(config, "")
            val engine = Core.engine as TxPylonsDevEngine
            engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
            UserData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to key)
            engine.cryptoHandler.importKeysFromUserData()
            Core.userProfile = Profile(engine.generateCredentialsFromKeys(), mutableMapOf(), listOf(), listOf(), lockedCoinDetails = LockedCoinDetails("", listOf(), listOf(), listOf()))
        }
    }

    /*
        Refused tx error with json error key
    */
    @Test
    fun error() {
        val tx = Core.userProfile?.credentials?.address?.let {
            Core.engine.sendItems(it, "abc", listOf())
        }
        tx?.submit()

        if (tx == null) {
            fail("tx is null")
        }

        Assertions.assertEquals(null, tx.id)
        Assertions.assertEquals(Transaction.State.TX_REFUSED, tx.state)
        Assertions.assertEquals(Transaction.ResponseCode.UNKNOWN_ERROR, tx.code)
        Assertions.assertEquals("decoding bech32 failed: invalid bech32 string length 3", tx.raw_log)
    }

    /*
        Refused tx error with code and raw_log error
    */
    @Test
    fun codeRawLogError() {
        val tx = Core.engine.createTrade(listOf(CoinInput("pylon", 5)), listOf(), listOf(), listOf(), "")
        tx.submit()

        Assertions.assertEquals(null, tx.id)
        Assertions.assertEquals(Transaction.State.TX_REFUSED, tx.state)
        Assertions.assertEquals(18, tx.code)
        Assertions.assertEquals("invalid request: sender not providing anything in exchange of the trade: empty outputs", tx.raw_log)
    }

    /*
        Refused tx error signature verification error
    */
    @Test
    fun signatureVerificationError() {
        val tx = Core.engine.applyRecipe("", arrayOf())
        tx.submit()

        Assertions.assertEquals(null, tx.id)
        Assertions.assertEquals(Transaction.State.TX_REFUSED, tx.state)
        Assertions.assertEquals(4, tx.code)
        Assertions.assertEquals("unauthorized: signature verification failed; verify correct account sequence and chain-id", tx.raw_log)
    }

    /*
        Accepted tx errors
        Get tx to examine error
    */
    @Test
    fun acceptedTXError() {
        runBlocking {
            Core.engine.getOwnBalances()
            val tx = Core.engine.applyRecipe("", arrayOf())
            tx.submit()

            Assertions.assertNotEquals(null, tx.id)
            Assertions.assertEquals(Transaction.State.TX_ACCEPTED, tx.state)
            Assertions.assertEquals(Transaction.ResponseCode.OK, tx.code)
            Assertions.assertEquals("", tx.raw_log)

            delay(5000)

            val txResult = tx.id?.let { Core.getTransaction(it) } ?: fail("txResult is null")

            Assertions.assertEquals(tx.id, txResult.id)
            Assertions.assertEquals(18, txResult.code)
            Assertions.assertEquals("invalid request: The recipe doesn't exist: failed to execute message; message index: 0", txResult.raw_log)
        }
    }
}