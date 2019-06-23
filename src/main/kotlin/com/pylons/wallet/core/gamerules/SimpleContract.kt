package com.pylons.wallet.core.gamerules

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.internal.newItemFromPrototype
import com.pylons.wallet.core.engine.OutsideWorldDummy
import com.pylons.wallet.core.engine.TxDummyEngine
import com.pylons.wallet.core.types.*

open class SimpleContract (val preferredItemIds: Set<String>) : GameRule() {

    fun <T> List<T>?.orEmpty(): List<T> {
        return when (this) {
            null -> listOf()
            else -> this
        }
    }

    override fun applyOffline() {
//        System.out.println("Applying contract $address offline")
//        val outItems = mutableListOf<Item>()
//        Core.userProfile!!.items.removeAll(boundItemsIn)
//        var actualItemsOut = itemsOut.orEmpty().toMutableList()
//        var actualCoinsOut = coinsOut.orEmpty().toMutableList()
//        System.out.println("Bwuh? ${lootTables == null}")
//        lootTables.orEmpty().forEach {
//            var entry = it.getRandomEntry()!!
//            System.out.println("...Bwuh?")
//            actualCoinsOut.addAll(entry.coins.orEmpty())
//            actualItemsOut.addAll(entry.items.orEmpty())
//        }
//        System.out.println("Bwuh?!?")
//        actualItemsOut.orEmpty().forEach {
//            var item = newItemFromPrototype(it)
//            Core.userProfile!!.items.add(item)
//            outItems.add(item)
//        }
//        coinsIn.orEmpty().forEach { Core.userProfile!!.coins[it.address] = Core.userProfile!!.coins[it.address]!! - it.count }
//        actualCoinsOut.orEmpty().forEach {
//            val base = when (Core.userProfile!!.coins[it.address]) {
//                null -> 0
//                else -> Core.userProfile!!.coins[it.address]!!
//            }
//            Core.userProfile!!.coins[it.address] = base + it.count
//        }
//        val txId = Core.engine!!.getNewTransactionId()
//        OutsideWorldDummy.addTx(Transaction(txId, (Core.userProfile!!.credentials as TxDummyEngine.Credentials).address, txId, coinsIn.orEmpty(), actualCoinsOut.orEmpty(),
//                boundItemsIn, outItems, Transaction.State.TX_ACCEPTED, boundCoinsCatalysts, boundItemsCatalysts))
    }

    override fun bindInputsAndCatalysts() : Boolean {
        if (Core.userProfile!!.canPayCoins(coinsIn.orEmpty())) boundCoinsIn = coinsIn.orEmpty()
        else return false
        System.out.println("Coin inputs are payable")
        val mItemsIn = mutableListOf<Item>()
        itemsIn.orEmpty().forEach {
            val item = Core.userProfile!!.findItemForPrototype(it, emptySet()) ?: return false
            mItemsIn.add(item)
        }
        boundItemsIn = mItemsIn
        System.out.println("Item inputs are payable")
        if (Core.userProfile!!.canPayCoins(coinCatalysts.orEmpty())) boundCoinsCatalysts = coinCatalysts.orEmpty()
        else return false
        System.out.println("Coin catalysts are payable")
        val mItemCatalysts = mutableListOf<Item>()
        itemCatalysts.orEmpty().forEach {
            val item = Core.userProfile!!.findItemForPrototype(it, emptySet()) ?: return false
            mItemCatalysts.add(item)
        }
        boundItemsCatalysts = mItemCatalysts
        System.out.println("Item catalysts are payable")
        return true
    }

    override fun canApply(): Boolean {
        return bindInputsAndCatalysts()
    }
}