package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Moshi

internal val moshi = Moshi.Builder().build()

private const val ASCII_SPACE = ' '
private const val ASCII_CR = '\r'
private const val ASCII_LF = '\n'
private const val ASCII_CODE_TAB = 8
private const val ASCII_CODE_SAFE_SPACE = 20
private const val ASCII_CODE_SAFE_CR = 21
private const val ASCII_CODE_SAFE_LF = 22
private const val ASCII_CODE_SAFE_TAB = 23

private fun String.protectCr () = this.replace(ASCII_CR, ASCII_CODE_SAFE_CR.toChar())
private fun String.protectLf () = this.replace(ASCII_LF, ASCII_CODE_SAFE_LF.toChar())
private fun String.protectSpace () = this.replace(ASCII_SPACE, ASCII_CODE_SAFE_SPACE.toChar())
private fun String.protectTab () = this.replace(ASCII_CODE_TAB.toChar(), ASCII_CODE_SAFE_TAB.toChar())

private fun String.unprotectCr () = this.replace(ASCII_CODE_SAFE_CR.toChar(), ASCII_CR)
private fun String.unprotectLf () = this.replace(ASCII_CODE_SAFE_LF.toChar(), ASCII_LF)
private fun String.unprotectSpace () = this.replace(ASCII_CODE_SAFE_SPACE.toChar(), ASCII_SPACE)
private fun String.unprotectTab () = this.replace(ASCII_CODE_SAFE_TAB.toChar(), ASCII_CODE_TAB.toChar())

fun protectWhitespace(input : String) = input.protectCr().protectLf().protectSpace().protectTab()
fun unprotectWhitespace(input : String) = input.unprotectCr().unprotectLf().unprotectSpace().unprotectTab()