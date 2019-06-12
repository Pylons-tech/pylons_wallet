package com.pylons.wallet.core.Infixes

import kotlin.experimental.and
import kotlin.experimental.xor

infix inline fun Byte.shl(that: Int): Byte = (this.toInt().shl(that)).toByte()
infix inline fun Int.shl(that: Byte): Byte = (this.shl(that.toInt())).toByte()
infix inline fun Byte.shl(that: Byte): Byte = (this.toInt().shl(that.toInt())).toByte()
infix inline fun Byte.shr(that: Int): Byte = (this.toInt().shr(that)).toByte()
infix inline fun Int.shr(that: Byte): Byte = (this.shr(that.toInt())).toByte()
infix inline fun Byte.shr(that: Byte): Byte = (this.toInt().shr(that.toInt())).toByte()
infix inline fun Byte.or(that: Int): Byte = (this.toInt().and(that)).toByte()
infix inline fun Int.or(that: Byte): Byte = (this.and(that.toInt())).toByte()
infix inline fun Byte.or(that: Byte): Byte = (this.toInt().and(that.toInt())).toByte()
infix inline fun Char.flip (bit : Int) : Char =
        this.toByte().xor((this.toByte().shl(bit).and(128.toByte())).xor(128.toByte()).shr(bit)).toChar()