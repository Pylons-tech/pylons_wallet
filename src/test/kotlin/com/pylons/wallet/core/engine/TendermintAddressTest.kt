package com.pylons.wallet.core.engine

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import org.apache.tuweni.bytes.Bytes32
import org.apache.tuweni.crypto.SECP256K1

internal class TendermintAddressTest {
    private val dsPrivate = "a96e62ed3955e65be32703f12d87b6b5cf26039ecfa948dc5107a495418e5330"
    private val dsPublic = "02950e1cdfcb133d6024109fd489f734eeb4502418e538c28481f22bce276f248c"
    private val dsAddr = "1CKZ9Nx4zgds8tU7nJHotKSDr4a9bYJCa3"

    @Test
    fun generatesAddress () {
        var keyPair = SECP256K1.KeyPair.create(
                SECP256K1.SecretKey.fromBytes(Bytes32.fromHexString(dsPrivate)),
                SECP256K1.PublicKey.fromBytes(Bytes32.fromHexString(dsPublic)))
        assertEquals(dsAddr, CryptoCosmos.getAddressFromKeyPair(keyPair).toHexString())
    }
}