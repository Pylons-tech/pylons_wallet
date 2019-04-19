package walletcore.gamerules

import walletcore.Core
import walletcore.internal.newItemFromPrototype
import walletcore.tx.OutsideWorldDummy
import walletcore.types.*

open class SimpleContract (val preferredItemIds: Set<String>) : GameRule() {

    override fun applyOffline() {
        val coins = Core.userProfile!!.coins.addCoins(coinsOut, false).addCoins(coinsIn, true)
        val outItems = mutableSetOf<Item>()
        itemsOut.forEach {
            outItems.add(newItemFromPrototype(it))
        }
        val items = Core.userProfile!!.items.exclude(boundItemsIn) + outItems
        Core.userProfile = Profile(id = Core.userProfile!!.id, strings = Core.userProfile!!.strings,
                coins = coins, items = items)
        val txid = Core.txHandler.getNewTransactionId()
        OutsideWorldDummy.addTx(Transaction(txid, Core.userProfile!!.id, txid, coinsIn, coinsOut,
                boundItemsIn, outItems, Transaction.State.TX_ACCEPTED, boundCoinsCatalysts, boundItemsCatalysts))
    }

    override fun bindInputsAndCatalysts() : Boolean {
        if (Core.userProfile!!.canPayCoins(coinsIn)) boundCoinsIn = coinsIn
        else return false
        val mItemsIn = mutableSetOf<Item>()
        itemsIn.forEach {
            val item = Core.userProfile!!.findItemForPrototype(it, emptySet()) ?: return false
            mItemsIn.add(item)
        }
        boundItemsIn = mItemsIn
        if (Core.userProfile!!.canPayCoins(coinCatalysts)) boundCoinsCatalysts = coinCatalysts
        else return false
        val mItemCatalysts = mutableSetOf<Item>()
        itemCatalysts.forEach {
            val item = Core.userProfile!!.findItemForPrototype(it, emptySet()) ?: return false
            mItemCatalysts.add(item)
        }
        boundItemsCatalysts = mItemCatalysts
        return true
    }

    override fun canApply(): Boolean {
        return bindInputsAndCatalysts()
    }
}