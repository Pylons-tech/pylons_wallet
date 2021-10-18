package tech.pylons.wallet.walletcore_test.types.transaction

import tech.pylons.wallet.core.Core
import tech.pylons.wallet.core.engine.TxPylonsDevEngine
import tech.pylons.wallet.core.engine.crypto.CryptoCosmos
import tech.pylons.lib.types.*
import tech.pylons.lib.types.tx.recipe.CoinInput
import tech.pylons.wallet.core.internal.InternalPrivKeyStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import tech.pylons.lib.types.tx.Coin
import java.lang.Exception

class TransactionErrorTest {
    val core = Core(Config(Backend.MANUAL, "pylonschain",true, listOf("http://127.0.0.1:1317"))).use()

    private val key = InternalPrivKeyStore.BANK_TEST_KEY

    init {
        if (!core.isReady()) {
            core.start("")
            val engine = core.engine as TxPylonsDevEngine
            engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
            core.userData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to key)
            engine.cryptoHandler.importKeysFromUserData()
            core.userProfile = MyProfile.getDefault(core)
        }
    }

    /*
        Refused tx error with json error key
    */
    @Test
    fun error() {
        val tx = try {
            core.engine.sendItems("abc", listOf()).submit()
        } catch (e : NullPointerException) {
            fail("tx is null")
        } catch (e : Exception) {
            throw e
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
        val tx = core.engine.createTrade("Creator", listOf(CoinInput(listOf(Coin("upylon", 5)))), listOf(), listOf(), listOf(), "")
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
        val tx = core.engine.applyRecipe("creator", "cookbookId", "id", 1, listOf())
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
            core.engine.getMyProfileState()
            val tx = core.engine.applyRecipe("creator", "cookbookId", "id", 1, listOf())
            tx.submit()
            println("waiting for tx to resolve")
            while (true) {
                if (tx.state == Transaction.State.TX_NOT_YET_SENT) Thread.sleep(5000)
                else break
            }
            assert(tx.id != null)
            Assertions.assertNotEquals("", tx.id)
            Assertions.assertEquals(Transaction.State.TX_ACCEPTED, tx.state)
            Assertions.assertEquals(Transaction.ResponseCode.OK, tx.code)
            Assertions.assertEquals("", tx.raw_log)

            delay(5000)

            val txResult = tx.id?.let { core.getTransaction(it) } ?: fail("txResult is null")

            Assertions.assertEquals(tx.id, txResult.id)
            Assertions.assertEquals(18, txResult.code)
            Assertions.assertEquals("invalid request: The recipe doesn't exist: failed to execute message; message index: 0", txResult.raw_log)
        }
    }
}