package com.pylons.wallet.core.engine

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.crypto.CryptoDummy
import com.pylons.wallet.core.types.*

internal class TxDummyTest {
    /**
     * TxDummyEngine always commits transactions instantly.
     */
    @Test
    fun commitTx() {
        val txDummy = TxDummyEngine()
        val tx = Transaction()
        Core.start(Backend.DUMMY, "")
        Core.userProfile = Profile.fromUserData()
        txDummy.commitTx(tx)
        assertEquals(Transaction.State.TX_ACCEPTED, tx.state)
    }

    /**
     * TxDummyEngine produces CryptoDummy CryptoHandlers.
     */
    @Test
    fun getNewCryptoHandler() {
        val cryptoHandler = TxDummyEngine().getNewCryptoHandler()
        assertNotNull(cryptoHandler)
        assertEquals(CryptoDummy::class, cryptoHandler::class)
    }
}