package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Coin
import com.pylons.wallet.core.types.Transaction
import com.pylons.wallet.core.types.tx.item.Item
import com.pylons.wallet.core.types.tx.recipe.*
import com.pylons.wallet.core.types.tx.trade.TradeItemInput

@ExperimentalUnsignedTypes
fun Core.createTrade (coinInputs: List<String>, itemInputs : List<String>,
                      coinOutputs : List<String>, itemOutputs : List<String>,
                      extraInfo : String) : Transaction {
    val mItemInputs = mutableListOf<TradeItemInput>()
    itemInputs.forEach { mItemInputs.add(TradeItemInput.fromJson(klaxon.parse(it)!!)) }
    val mCoinInputs = mutableListOf<CoinInput>()
    coinInputs.forEach { mCoinInputs.add(CoinInput.fromJson(klaxon.parse(it)!!)) }
    val mCoinOutputs = mutableListOf<Coin>()
    coinOutputs.forEach { mCoinOutputs.add(Coin.fromJson(klaxon.parse(it)!!)) }
    val mItemOutputs = mutableListOf<Item>()
    itemOutputs.forEach { mItemOutputs.add(Item.fromJson(klaxon.parse(it)!!)) }
    return engine.createTrade(mCoinInputs, mItemInputs, mCoinOutputs, mItemOutputs, extraInfo).submit()
}