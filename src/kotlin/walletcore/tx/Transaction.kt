package walletcore.tx

import walletcore.types.Coin
import walletcore.types.Event
import walletcore.types.Item

/**
 * Models a transaction. Internally, transactions are just sets of inputs and outputs
 */
data class Transaction(
        val id: String = "",
        val coinsIn: List<Coin> = listOf(),
        val coinsOut: List<Coin> = listOf(),
        val itemsIn: List<Item> = listOf(),
        val itemsOut: List<Item> = listOf(),
        var state: State = State.TX_NOT_YET_SENT
) {
    val onResolved = Event<Transaction>()
    val onSubmitted = Event<Transaction>()

    enum class State(val value: Int) {
        TX_REFUSED(-1),
        TX_NOT_YET_SENT(0),
        TX_NOT_YET_COMMITTED(1),
        TX_ACCEPTED(2),
    }

    fun finish(newState: State) {
        state = newState
        when (state) {
            State.TX_NOT_YET_SENT -> throw Exception("Transaction.finish should never be called with" +
                    "State.TX_NOT_YET_SENT as an argument. Debug this.")
            State.TX_NOT_YET_COMMITTED -> throw Exception("Transaction.finish should never be called with" +
                    "State.TX_NOT_YET_COMMITTED as an argument. Debug this.")
            State.TX_ACCEPTED -> onResolved.onSuccess(this)
            State.TX_REFUSED -> onResolved.onFailure(this)
        }
    }

    fun submit() {
        if (state != State.TX_NOT_YET_SENT) throw Exception("Transaction.submit() should only be called on" +
                "Transactions of state TX_NOT_YET_SEND")
        state = State.TX_NOT_YET_COMMITTED
        onSubmitted.onSuccess(this)
    }
}