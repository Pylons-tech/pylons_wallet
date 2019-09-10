package com.pylons.wallet.core.types.item.prototype

data class DoubleParam(
    val minValue : Double,
    val maxValue : Double,
    val rate : Double,
    val paramType : ParamType
) {
    fun toOutputJson (quoteNumerals : Boolean) : String {
        return if (quoteNumerals) {
            """{"MaxValue":"$maxValue","MinValue":"$minValue","Rate":"$rate"}"""
        }
        else {
            """{"MaxValue":$maxValue,"MinValue":$minValue,"Rate":$rate}"""
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