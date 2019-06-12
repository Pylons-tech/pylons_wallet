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

infix inline fun UByte.shl(that: Int): UByte = (this.toInt().shl(that)).toUByte()
infix inline fun Int.shl(that: UByte): UByte = (this.shl(that.toInt())).toUByte()
infix inline fun UByte.shl(that: UByte): UByte = (this.toInt().shl(that.toInt())).toUByte()
infix inline fun UByte.shr(that: Int): UByte = (this.toInt().shr(that)).toUByte()
infix inline fun Int.shr(that: UByte): UByte = (this.shr(that.toInt())).toUByte()
infix inline fun UByte.shr(that: UByte): UByte = (this.toInt().shr(that.toInt())).toUByte()
infix inline fun UByte.or(that: Int): UByte = (this.toInt().and(that)).toUByte()
infix inline fun Int.or(that: UByte): UByte = (this.and(that.toInt())).toUByte()
infix inline fun UByte.or(that: UByte): UByte = (this.toInt().and(that.toInt())).toUByte()
