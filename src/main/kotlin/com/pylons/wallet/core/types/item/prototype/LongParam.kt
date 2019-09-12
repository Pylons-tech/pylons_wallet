package com.pylons.wallet.core.types.item.prototype

import com.pylons.wallet.core.types.txJson.s

data class LongParam(
    val minValue : Long,
    val maxValue : Long,
    val rate : Double,
    val paramType : ParamType
) {
    fun toOutputJson (quoteNumerals : Boolean) : String {
        return if (quoteNumerals) {
            """{"MaxValue":"$maxValue","MinValue":"$minValue","Rate":"${rate.s()}"}"""
        }
        else {
            """{"MaxValue":$maxValue,"MinValue":$minValue,"Rate":${rate.s()}}"""
        }
    }

    fun toInputJson (quoteNumerals : Boolean) : String {
        return if (quoteNumerals) {
            """{"MaxValue":"$maxValue","MinValue":"$minValue"}"""
        }
        else {
            """{"MaxValue":$maxValue,"MinValue":$minValue}"""
        }
    }
}