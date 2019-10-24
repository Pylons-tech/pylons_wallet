package com.pylons.wallet.core.types

import com.pylons.wallet.core.types.item.prototype.ItemPrototype
import com.pylons.wallet.core.types.jsonTemplate.getCoinIOListForMessage
import com.pylons.wallet.core.types.jsonTemplate.getCoinIOListForSigning
import com.pylons.wallet.core.types.jsonTemplate.getItemInputListForMessage
import com.pylons.wallet.core.types.jsonTemplate.getItemInputListForSigning

data class ParamSet(
        val itemOutputs : List<ItemPrototype>,
        val coinOutputs : Map<String, Long>
) {
    fun toJson (forSigning : Boolean) : String {
        return when (forSigning) {
            true -> """{"CoinOutputs":${getCoinIOListForSigning(coinOutputs)},"ItemOutputs":${getItemInputListForSigning(itemOutputs.toTypedArray())}}"""
            false -> """{"CoinOutputs":${getCoinIOListForMessage(coinOutputs)},"ItemOutputs":${getItemInputListForMessage(itemOutputs.toTypedArray())}}"""
        }
    }
}