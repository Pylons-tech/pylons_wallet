package walletcore.tx

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import walletcore.Core
import walletcore.crypto.CryptoDummy
import walletcore.types.*

internal class TxDummyTest {
    /**
     * TxDummy always commits transactions instantly.
     */
    @Test
    fun commitTx() {
        val txDummy = TxDummy()
        val tx = Transaction()
        Core.start()
        Core.setProfile(Profile())
        txDummy.commitTx(tx)
        assertEquals(Transaction.State.TX_ACCEPTED, tx.state)
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