package walletcore.gamerules

import walletcore.Core
import walletcore.internal.newItemFromPrototype
import walletcore.tx.OutsideWorldDummy
import walletcore.types.*

open class SimpleContract (val preferredItemIds: Set<String>) : GameRule() {

    override fun applyOffline() {
        System.out.println("Applying contract $id offline")
        val outItems = mutableSetOf<Item>()
        Core.userProfile!!.items.removeAll(boundItemsIn)
        itemsOut.orEmpty().forEach { Core.userProfile!!.items.add(newItemFromPrototype(it)) }
        coinsIn.orEmpty().forEach { Core.userProfile!!.coins[it.id] = Core.userProfile!!.coins[it.id]!! - it.count!! }
        coinsOut.orEmpty().forEach {
            val base = when (Core.userProfile!!.coins[it.id]) {
                null -> 0
                else -> Core.userProfile!!.coins[it.id]!!
            }
            Core.userProfile!!.coins[it.id] = base + it.count!! }
        val txId = Core.txHandler.getNewTransactionId()
        OutsideWorldDummy.addTx(Transaction(txId, Core.userProfile!!.id, txId, coinsIn.orEmpty(), coinsOut.orEmpty(),
                boundItemsIn, outItems, Transaction.State.TX_ACCEPTED, boundCoinsCatalysts, boundItemsCatalysts))
    }

    override fun bindInputsAndCatalysts() : Boolean {
        if (Core.userProfile!!.canPayCoins(coinsIn.orEmpty())) boundCoinsIn = coinsIn.orEmpty()
        else return false
        System.out.println("Coin inputs are payable")
        val mItemsIn = mutableSetOf<Item>()
        itemsIn.orEmpty().forEach {
            val item = Core.userProfile!!.findItemForPrototype(it, emptySet()) ?: return false
            mItemsIn.add(item)
        }
        boundItemsIn = mItemsIn
        System.out.println("Item inputs are payable")
        if (Core.userProfile!!.canPayCoins(coinCatalysts.orEmpty())) boundCoinsCatalysts = coinCatalysts.orEmpty()
        else return false
        System.out.println("Coin catalysts are payable")
        val mItemCatalysts = mutableSetOf<Item>()
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