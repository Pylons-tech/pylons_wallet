package walletcore.tx

import walletcore.types.Callback
import walletcore.types.Coin
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
    val state: State = State.TX_NOT_YET_SENT,
    var onSuccess: Callback<Transaction>? = null,
    var onFailure: Callback<Transaction>? = null
) {
    enum class State(val value: Int) {
        TX_REFUSED(-1),
        TX_NOT_YET_SENT(0),
        TX_NOT_YET_COMMITTED(1),
        TX_ACCEPTED(2),
    }
}