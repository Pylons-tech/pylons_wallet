package com.pylons.wallet.core.engine.crypto

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Backend
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.types.UserData

internal class CryptoDummyTest {
    val garbage_0 = byteArrayOf(9, 9, 9, 4, 4, 4, 3, 3)
    val garbage_1 = byteArrayOf(9, 9, 3, 4, 2, 2, 3, 3)
    val garbage_2 = byteArrayOf(5, 9, 0xF, 4, 4, 4, 0, 3)

    /**
     * Crypto dummy just gives an empty bytearray when you tell it to sign... anything.
     */
    @Test
    fun signature() {
        Core.start(Backend.DUMMY, "")
        val cryptoDummy = Core.engine.getNewCryptoHandler()
        assertArrayEquals(byteArrayOf(), cryptoDummy.signature(garbage_0))
        assertArrayEquals(byteArrayOf(), cryptoDummy.signature(garbage_1))
        assertArrayEquals(byteArrayOf(), cryptoDummy.signature(garbage_2))
    }

    /**
     * Crypto dummy always verifies whatever you give it.
     */
    @Test
    fun verify() {
        Core.start(Backend.DUMMY, "")
        val cryptoDummy = Core.engine.getNewCryptoHandler()

        assertTrue(cryptoDummy.verify(garbage_1, byteArrayOf()))
        assertTrue(cryptoDummy.verify(garbage_1, garbage_2))
    }
}