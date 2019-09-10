package com.pylons.wallet.core.types.item.prototype

data class LongParam(
    val minValue : Long,
    val maxValue : Long,
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