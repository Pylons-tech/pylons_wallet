package com.pylons.wallet.core

import com.pylons.wallet.core.infixes.shl
import com.pylons.wallet.core.infixes.shr
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@kotlin.ExperimentalUnsignedTypes
internal class InfixTest {
    val shl = mapOf(
            // 0111 1111 to 1111 1110
            127.toUByte() to 254.toUByte(),
            // 0000 0001 to 0000 0010
            1.toUByte() to 2.toUByte(),
            // 1111 1111 to 1111 1110
            255.toUByte() to 254.toUByte(),
            // 1000 0000 to 0000 0000
            128.toUByte() to 0.toUByte()
    )

    val shr = mapOf(
            // 1111 1110 to 0111 1111
            254.toUByte() to 127.toUByte(),
            // 0000 0010 to 0000 0001
            2.toUByte() to 1.toUByte(),
            // 1111 1110 to 0111 1111
            255.toUByte() to 127.toUByte(),
            // 1000 0000 to 0100 0000
            128.toUByte() to 64.toUByte()
    )


    @Test
    fun bitshiftOnUBytes () {
        for (test in shl) assertEquals(test.key.shl(1), test.value,
                "Left shift on ${test.key} yielded ${test.key.shl(1)}, expected " +
                        "${test.value}")
        for (test in shr) assertEquals(test.key.shr(1), test.value,
                "Right shift on ${test.key} yielded ${test.key.shr(1)}, expected " +
                        "${test.value}")
    }
}