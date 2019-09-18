package com.pylons.wallet.core.types.item.prototype

import com.pylons.wallet.core.types.txJson.s

data class DoubleParam(
    val minValue : Double,
    val maxValue : Double,
    val rate : Double,
    val paramType : ParamType
) {
    fun toOutputJson () : String = """{"MaxValue":"${maxValue.s()}","MinValue":"${minValue.s()}","Rate":"${rate.s()}"}"""

    fun toInputJson () : String = """{"MaxValue":"${maxValue.s()}","MinValue":"${minValue.s()}"}"""
}