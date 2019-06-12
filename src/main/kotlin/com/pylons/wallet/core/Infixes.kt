package com.pylons.wallet.core.infixes

inline infix fun Byte.shl(that: Int): Byte = (this.toInt().shl(that)).toByte()
inline infix fun Int.shl(that: Byte): Byte = (this.shl(that.toInt())).toByte()
inline infix fun Byte.shl(that: Byte): Byte = (this.toInt().shl(that.toInt())).toByte()
inline infix fun Byte.shr(that: Int): Byte = (this.toInt().shr(that)).toByte()
inline infix fun Int.shr(that: Byte): Byte = (this.shr(that.toInt())).toByte()
inline infix fun Byte.shr(that: Byte): Byte = (this.toInt().shr(that.toInt())).toByte()
inline infix fun Byte.or(that: Int): Byte = (this.toInt().and(that)).toByte()
inline infix fun Int.or(that: Byte): Byte = (this.and(that.toInt())).toByte()
inline infix fun Byte.or(that: Byte): Byte = (this.toInt().and(that.toInt())).toByte()

@kotlin.ExperimentalUnsignedTypes
inline infix fun UByte.shl(that: Int): UByte = (this.toInt().shl(that)).toUByte()
@kotlin.ExperimentalUnsignedTypes
inline infix fun Int.shl(that: UByte): UByte = (this.shl(that.toInt())).toUByte()
@kotlin.ExperimentalUnsignedTypes
inline infix fun UByte.shl(that: UByte): UByte = (this.toInt().shl(that.toInt())).toUByte()
@kotlin.ExperimentalUnsignedTypes
inline infix fun UByte.shr(that: Int): UByte = (this.toInt().shr(that)).toUByte()
@kotlin.ExperimentalUnsignedTypes
inline infix fun Int.shr(that: UByte): UByte = (this.shr(that.toInt())).toUByte()
@kotlin.ExperimentalUnsignedTypes
inline infix fun UByte.shr(that: UByte): UByte = (this.toInt().shr(that.toInt())).toUByte()
@kotlin.ExperimentalUnsignedTypes
inline infix fun UByte.or(that: Int): UByte = (this.toInt().and(that)).toUByte()
@kotlin.ExperimentalUnsignedTypes
inline infix fun Int.or(that: UByte): UByte = (this.and(that.toInt())).toUByte()
@kotlin.ExperimentalUnsignedTypes
inline infix fun UByte.or(that: UByte): UByte = (this.toInt().and(that.toInt())).toUByte()
