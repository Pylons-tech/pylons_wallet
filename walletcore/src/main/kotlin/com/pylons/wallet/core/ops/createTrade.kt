package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Transaction
import com.pylons.wallet.core.types.tx.item.Item
import com.pylons.wallet.core.types.tx.recipe.*

@ExperimentalUnsignedTypes
fun Core.createTrade (coinInputs: List<String>, itemInputs : List<String>,
                      coinOutputs : List<String>, itemOutputs : List<String>,
                      extraInfo : String) : Transaction {
    val mItemInputs = mutableListOf<ItemInput>()
    itemInputs.forEach { mItemInputs.add(ItemInput.fromJson(klaxon.parse(it)!!)) }
    val mCoinInputs = mutableListOf<CoinInput>()
    coinInputs.forEach { mCoinInputs.add(CoinInput.fromJson(klaxon.parse(it)!!)) }
    val mCoinOutputs = mutableListOf<CoinOutput>()
    coinOutputs.forEach { mCoinOutputs.add(CoinOutput.fromJson(klaxon.parse(it)!!)) }
    val mItemOutputs = mutableListOf<Item>()
    itemOutputs.forEach { mItemOutputs.add(Item.fromJson(klaxon.parse(it)!!)) }
    return engine.createTrade(mCoinInputs, mItemInputs, mCoinOutputs, mItemOutputs, extraInfo)
}