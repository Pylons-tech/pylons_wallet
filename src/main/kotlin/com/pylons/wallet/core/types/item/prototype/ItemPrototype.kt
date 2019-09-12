package com.pylons.wallet.core.types.item.prototype

import java.lang.StringBuilder

data class ItemPrototype (
        val doubleParams : Map<String, DoubleParam> = mapOf(),
        val longParams : Map<String, LongParam> = mapOf(),
        val stringParams : Map<String, StringParam> = mapOf()
) {
    private fun getInputDoubleParamJson(quoteNumerals: Boolean) : String {
        val dParams = StringBuilder("{")
        doubleParams.forEach {
            if (it.value.paramType == ParamType.INPUT || it.value.paramType == ParamType.INPUT_OUTPUT) {
                dParams.append(""""${it.key}":${it.value.toInputJson(quoteNumerals)},""")
            }
        }
        if (dParams.length > 1) dParams.deleteCharAt(dParams.length - 1)
        dParams.append('}')
        return dParams.toString()
    }

    private fun getInputLongParamJson(quoteNumerals: Boolean) : String {
        val lParams = StringBuilder("{")
        longParams.forEach {
            if (it.value.paramType == ParamType.INPUT || it.value.paramType == ParamType.INPUT_OUTPUT) {
                lParams.append(""""${it.key}":${it.value.toInputJson(quoteNumerals)},""")
            }
        }
        if (lParams.length > 1) lParams.deleteCharAt(lParams.length - 1)
        lParams.append('}')
        return lParams.toString()
    }

    private fun getInputStringParamJson(quoteNumerals: Boolean) : String {
        val sParams = StringBuilder("{")
        stringParams.forEach {
            if (it.value.paramType == ParamType.INPUT || it.value.paramType == ParamType.INPUT_OUTPUT) {
                sParams.append(""""${it.key}":${it.value.toInputJson(quoteNumerals)},""")
            }
        }
        if (sParams.length > 1) sParams.deleteCharAt(sParams.length - 1)
        sParams.append('}')
        return sParams.toString()
    }

    private fun getOutputDoubleParamJson(quoteNumerals: Boolean) : String {
        val dParams = StringBuilder("{")
        doubleParams.forEach {
            if (it.value.paramType == ParamType.OUTPUT || it.value.paramType == ParamType.INPUT_OUTPUT) {
                dParams.append(""""${it.key}":${it.value.toOutputJson(quoteNumerals)},""")
            }
        }
        if (dParams.length > 1) dParams.deleteCharAt(dParams.length - 1)
        dParams.append('}')
        return dParams.toString()
    }

    private fun getOutputLongParamJson(quoteNumerals: Boolean) : String {
        val lParams = StringBuilder("{")
        longParams.forEach {
            if (it.value.paramType == ParamType.OUTPUT || it.value.paramType == ParamType.INPUT_OUTPUT) {
                lParams.append(""""${it.key}":${it.value.toOutputJson(quoteNumerals)},""")
            }
        }
        if (lParams.length > 1) lParams.deleteCharAt(lParams.length - 1)
        lParams.append('}')
        return lParams.toString()
    }

    private fun getOutputStringParamJson(quoteNumerals: Boolean) : String {
        val sParams = StringBuilder("{")
        stringParams.forEach {
            if (it.value.paramType == ParamType.OUTPUT || it.value.paramType == ParamType.INPUT_OUTPUT) {
                sParams.append(""""${it.key}":${it.value.toOutputJson(quoteNumerals)},""")
            }
        }
        if (sParams.length > 1) sParams.deleteCharAt(sParams.length - 1)
        sParams.append('}')
        return sParams.toString()
    }

    fun exportItemOutputJson (quoteNumerals : Boolean) : String =
            """{"Doubles":${getOutputDoubleParamJson(quoteNumerals)},"Longs":${getOutputLongParamJson(quoteNumerals)},"Strings":${getOutputStringParamJson(quoteNumerals)}}"""

    fun exportItemInputJson (quoteNumerals : Boolean) : String =
            """{"Doubles":${getInputDoubleParamJson(quoteNumerals)},"Longs":${getInputLongParamJson(quoteNumerals)},"Strings":${getInputStringParamJson(quoteNumerals)}}"""
}