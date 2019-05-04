package walletcore.gamerules

import walletcore.Core
import walletcore.internal.newItemFromPrototype
import walletcore.tx.OutsideWorldDummy
import walletcore.types.*

open class SimpleContract (val preferredItemIds: Set<String>) : GameRule() {

    fun <T> List<T>?.orEmpty(): List<T> {
        return when (this) {
            null -> listOf()
            else -> this
        }
    }

    override fun applyOffline() {
        System.out.println("Applying contract $id offline")
        val outItems = mutableListOf<Item>()
        Core.userProfile!!.items.removeAll(boundItemsIn)
        var actualItemsOut = itemsOut.orEmpty().toMutableList()
        var actualCoinsOut = coinsOut.orEmpty().toMutableList()
        if (lootTableOutput != null) {
            var entry = lootTableOutput!!.getRandomEntry()!!
            actualCoinsOut.addAll(entry.coins.orEmpty())
            actualItemsOut.addAll(entry.items.orEmpty())
        }
        actualItemsOut.orEmpty().forEach {
            var item = newItemFromPrototype(it)
            Core.userProfile!!.items.add(item)
            outItems.add(item)
        }
        coinsIn.orEmpty().forEach { Core.userProfile!!.coins[it.id] = Core.userProfile!!.coins[it.id]!! - it.count!! }
        actualCoinsOut.orEmpty().forEach {
            val base = when (Core.userProfile!!.coins[it.id]) {
                null -> 0
                else -> Core.userProfile!!.coins[it.id]!!
            }
            Core.userProfile!!.coins[it.id] = base + it.count!! }
        val txId = Core.txHandler.getNewTransactionId()
        OutsideWorldDummy.addTx(Transaction(txId, Core.userProfile!!.id, txId, coinsIn.orEmpty(), actualCoinsOut.orEmpty(),
                boundItemsIn, outItems, Transaction.State.TX_ACCEPTED, boundCoinsCatalysts, boundItemsCatalysts))
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