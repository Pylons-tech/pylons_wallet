package walletcore.types

import walletcore.Core
import java.lang.NullPointerException

/**
 * Models a transaction. Internally, transactions are just sets of inputs and outputs
 */
data class Transaction(
        val txId: String = "",
        val otherProfileId: String = "",
        val coinsIn: Set<Coin> = setOf(),
        val coinsOut: Set<Coin> = setOf(),
        val itemsIn: Set<Item> = setOf(),
        val itemsOut: Set<Item> = setOf(),
        var state: State = State.TX_NOT_YET_SENT
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
                val itemsIn = mutableSetOf<Item>()
                val itemsOut = mutableSetOf<Item>()
                txDescription.itemsInIds.forEach {
                    itemsIn.add(Item.findInLocalProfile(it)!!)
                }
                txDescription.itemsOutIds.forEach {
                    itemsOut.add(Item.findInBufferedForeignProfile(txDescription.otherProfileId, it)!!)
                }
                Transaction(Core.txHandler.getNewTransactionId(), txDescription.otherProfileId, txDescription.coinsIn, txDescription.coinsOut,
                        itemsIn.toSet(), itemsOut.toSet())
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
}