package com.pylons.wallet.core.types.item.prototype

import com.pylons.wallet.core.types.txJson.s

data class DoubleParam(
    val minValue : Double,
    val maxValue : Double,
    val rate : Double,
    val paramType : ParamType
) {
    fun toOutputJson (quoteNumerals : Boolean) : String {
        return if (quoteNumerals) {
            """{"MaxValue":"${maxValue.s()}","MinValue":"${minValue.s()}","Rate":"${rate.s()}"}"""
        }
        else {
            """{"MaxValue":${maxValue.s()},"MinValue":${minValue.s()},"Rate":${rate.s()}}"""
        }
    }

    fun toInputJson (quoteNumerals : Boolean) : String {
        return if (quoteNumerals) {
            """{"MaxValue":"${maxValue.s()}","MinValue":"${minValue.s()}"}"""
        }
        else {
            """{"MaxValue":${maxValue.s()},"MinValue":${minValue.s()}}"""
        }
    }
}