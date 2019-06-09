package com.pylons.wallet.core.types

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PylonsBech32Test {

    @Test
    fun fromKey() {
        assertEquals("A7AqbjDky/lrA1bj7bEW3vww8Kc4IDXIb76w3Zg+DTKj", "cosmos146yrz0p79pm6xd33nr3ajtxv2206rvcx0rs2c6")
    }
}
