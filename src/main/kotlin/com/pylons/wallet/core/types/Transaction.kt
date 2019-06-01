package com.pylons.wallet.core.types

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import java.lang.NullPointerException

/**
 * Models a transaction. Internally, transactions are just sets of inputs and outputs
 */
data class Transaction(
        val txId: String = "",
        val addressIn: String = "",
        val addressOut: String = "",
        val coinsIn: List<Coin> = listOf(),
        val coinsOut: List<Coin> = listOf(),
        val itemsIn: List<Item> = listOf(),
        val itemsOut: List<Item> = listOf(),
        var state: State = State.TX_NOT_YET_SENT,
        val coinsCatalysts: List<Coin> = listOf(),
        val itemsCatalysts: List<Item> = listOf()
) {
    enum class State(val value: Int) {
        TX_REFUSED(-1),
        TX_NOT_YET_SENT(0),
        TX_NOT_YET_COMMITTED(1),
        TX_ACCEPTED(2),
    }

    companion object {
        fun build (txDescription: TransactionDescription) : Transaction? {
            return try {
                val itemsIn = mutableListOf<Item>()
                val itemsOut = mutableListOf<Item>()
                txDescription.itemsInIds.forEach {
                    itemsIn.add(Item.findInLocalProfile(it)!!)
                }
                txDescription.itemsOutIds.forEach {
                    itemsOut.add(Item.findInBufferedForeignProfile(txDescription.otherProfileId, it)!!)
                }
                Transaction(Core.engine.getNewTransactionId(), Core.userProfile!!.id,
                        txDescription.otherProfileId, txDescription.coinsIn, txDescription.coinsOut,
                        itemsIn.toList(), itemsOut.toList())
            } catch (e : NullPointerException) {
                return null
            }
        }


    }

    fun finish(newState: State) {
        state = newState
        when (state) {
            State.TX_NOT_YET_SENT -> throw Exception("Transaction.finish should never be called with" +
                    "State.TX_NOT_YET_SENT as an argument. Debug this.")
            State.TX_NOT_YET_COMMITTED -> throw Exception("Transaction.finish should never be called with" +
                    "State.TX_NOT_YET_COMMITTED as an argument. Debug this.")
            //State.TX_ACCEPTED -> onResolved.onSuccess(this)
            //State.TX_REFUSED -> onResolved.onFailure(this)
        }

    }

    fun submit() {
        if (state != State.TX_NOT_YET_SENT) throw Exception("Transaction.submit() should only be called on" +
                "Transactions of state TX_NOT_YET_SEND")
        state = State.TX_NOT_YET_COMMITTED
        //onSubmitted.onSuccess(this)
    }

    fun detailsToMessageData() : MessageData {
        val msg = MessageData()
        msg.strings["txId"] = txId
        msg.strings[Keys.otherProfileId] = addressOut
        msg.strings[Keys.coinsIn] = coinsIn.serialize()
        msg.strings[Keys.coinsOut] = coinsOut.serialize()
        msg.stringArrays[Keys.itemsIn] = itemsIn.serialize()
        msg.stringArrays[Keys.itemsOut] = itemsOut.serialize()
        return msg
    }
}