package com.pylons.wallet.core.types

import com.lambdaworks.codec.Base64
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import org.apache.tuweni.bytes.Bytes
import org.apache.tuweni.crypto.SECP256K1
import org.bitcoinj.core.Bech32
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.StringBuilder

internal class PylonsBech32Test {
    val key = "A7AqbjDky/lrA1bj7bEW3vww8Kc4IDXIb76w3Zg+DTKj"
    val addr = "cosmos146yrz0p79pm6xd33nr3ajtxv2206rvcx0rs2c6"

    @Test
    fun decode(){
        val decoded = PylonsBech32.decode(addr)
        assertEquals("cosmos", decoded.hrp)
        val rawAddr = CryptoCosmos.getAddressFromPubkey(Bytes.wrap(Base64.decode(key.toCharArray())))
        assertEquals(rawAddr, decoded.data)
    }

    @Test
    fun fromKey() {
        assertEquals(PylonsBech32().fromKey(key), addr)
    }
}
