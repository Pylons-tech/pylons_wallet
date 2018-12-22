package walletcore.tx

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import walletcore.crypto.CryptoDummy
import walletcore.types.Callback
import walletcore.types.UserData

internal class TxDummyTest {

    /**
     * TxDummy always commits transactions instantly.
     */
    @Test
    fun commitTx() {
        val txDummy = TxDummy()
        val tx = Transaction()
        //tx.onResolved.register
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