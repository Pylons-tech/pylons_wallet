package com.pylons.wallet.core.types.item.prototype

import com.pylons.wallet.core.types.txJson.s
import java.lang.StringBuilder

data class LongParam(
    val weightRanges : List<WeightRange>,
    val rate : Double,
    val paramType : ParamType
) {
    data class WeightRange (
            val min : Long,
            val max : Long,
            val weight : Long
    ) {
        fun toJson (quoteNumerals : Boolean) : String {
            return if (quoteNumerals) {
                """{"Lower:"$min","Upper":"$max","Weight":"$weight"}"""
            }
            else {
                """{"Lower:$min,"Upper":$max,"Weight":$weight}"""
            }
        }
    }

    private fun weightTableArray (quoteNumerals: Boolean): String {
        val sb = StringBuilder("[")
        for (it in weightRanges) sb.append("${it.toJson(quoteNumerals)},")
        if (sb.length > 1) sb.setLength(sb.length - 1)
        sb.append("]")
        return sb.toString()
    }

    fun toOutputJson (quoteNumerals : Boolean) : String {
        return if (quoteNumerals) {
            """{"Rate":"${rate.s()}","WeightRanges":${weightTableArray(quoteNumerals)}}"""
        }
        else {
            """{"Rate":"${rate.s()}","WeightRanges":${weightTableArray(quoteNumerals)}}"""
        }
    }

    fun toInputJson (quoteNumerals : Boolean) : String {
        return if (quoteNumerals) {
            """{"WeightRanges":${weightTableArray(quoteNumerals)}}"""
        }
        else {
            """{"WeightRanges":${weightTableArray(quoteNumerals)}}"""
        }
    }
}