package com.pylons.wallet.core.types

import com.pylons.wallet.core.types.item.Item
import com.pylons.wallet.core.types.item.prototype.ItemPrototype
import com.pylons.wallet.core.types.txJson.getCoinIOListForMessage
import com.pylons.wallet.core.types.txJson.getCoinIOListForSigning
import com.pylons.wallet.core.types.txJson.getItemInputListForMessage
import com.pylons.wallet.core.types.txJson.getItemInputListForSigning

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