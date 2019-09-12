package com.pylons.wallet.core.types.item.prototype

import com.pylons.wallet.core.types.txJson.s

data class StringParam(
    val value : String,
    val rate : Double,
    val paramType : ParamType
) {
    fun toOutputJson (quoteNumerals : Boolean) : String {
        return when (quoteNumerals) {
            true -> """{"Rate":"${rate.s()}","Value":"$value"}"""
            false -> """{"Rate":${rate.s()},"Value":"$value"}"""
        }
    }

    fun toInputJson (quoteNumerals : Boolean) : String {
        return """{"Value":"$value"}"""
    }
}