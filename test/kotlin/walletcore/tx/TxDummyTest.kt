package walletcore.tx

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import walletcore.crypto.CryptoDummy
import walletcore.types.Callback
import walletcore.types.UserData

internal class TxDummyTest {

    class TestCallback : Callback<Transaction> {
        var fired = false

        override fun onSuccess(result: Transaction) {
            fired = true
        }

        override fun onFailure(result: Transaction) {
            assertTrue(false)
        }

        override fun onException(e: Exception?) {
            assertTrue(false)
        }
    }

    /**
     * TxDummy always commits transactions instantly.
     */
    @Test
    fun commitTx() {
        val txDummy = TxDummy()
        val tx = Transaction()
        val cb = TestCallback()
        tx.onResolved.register(cb)
        txDummy.commitTx(tx)
        assertTrue(cb.fired)
    }

    /**
     * TxDummy produces CryptoDummy CryptoHandlers.
     */
    @Test
    fun getNewCryptoHandler() {
        val cryptoHandler = TxDummy().getNewCryptoHandler(UserData())
        assertNotNull(cryptoHandler)
        assertEquals(CryptoDummy::class, cryptoHandler::class)
    }
}