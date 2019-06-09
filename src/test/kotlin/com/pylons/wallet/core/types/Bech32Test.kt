package com.pylons.wallet.core.types

import org.bitcoinj.core.Bech32
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PylonsBech32Test {

    val key = "A7AqbjDky/lrA1bj7bEW3vww8Kc4IDXIb76w3Zg+DTKj"
    val addr = "cosmos146yrz0p79pm6xd33nr3ajtxv2206rvcx0rs2c6"

    @Test
    fun decode(){
        val decoded = Bech32.decode(addr)
        assertEquals(key, decoded)
    }

    @Test
    fun fromKey() {
        assertEquals(PylonsBech32().fromKey(key), addr)
    }
}
