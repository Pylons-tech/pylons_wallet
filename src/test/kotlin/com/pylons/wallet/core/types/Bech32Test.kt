package com.pylons.wallet.core.types

import com.lambdaworks.codec.Base64
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import org.apache.tuweni.bytes.Bytes
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PylonsBech32Test {
    val key = "A7AqbjDky/lrA1bj7bEW3vww8Kc4IDXIb76w3Zg+DTKj"
    val addr = "cosmos146yrz0p79pm6xd33nr3ajtxv2206rvcx0rs2c6"

    @Test
    fun decode(){
        val decoded = Bech32Cosmos.decode(addr)
        assertEquals("cosmos", decoded.hrp)
        println(Bytes.wrap(Base64.decode(key.toCharArray())))
        val rawAddr = CryptoCosmos.getAddressFromPubkey(Bytes.wrap(Base64.decode(key.toCharArray())))
        assertEquals(rawAddr, decoded.data)
    }

    //@Test
    //fun fromKey() {
    //    assertEquals(Bech32Cosmos().fromKey(key), addr)
    //}
}
