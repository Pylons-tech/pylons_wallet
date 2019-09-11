package com.pylons.wallet.core.types.item.prototype

data class StringParam(
    val value : String,
    val rate : Double,
    val paramType : ParamType
) {
    fun toOutputJson (quoteNumerals : Boolean) : String {
        return """{"Rate":"$rate","Value":"$value"}"""
    }

    fun toInputJson (quoteNumerals : Boolean) : String {
        return """{"Value":"$value"}"""
    }
}