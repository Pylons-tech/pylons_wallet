package com.pylons.wallet.core.types

import com.jayway.jsonpath.JsonPath
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.tx.StdTx
import java.lang.NullPointerException


data class Transaction(
        val stdTx: StdTx? = null,
        val _id : String? = null,
        val resolver : ((Transaction) -> Unit)? = null,
        var state: State = State.TX_NOT_YET_SENT
) {
    var id : String? = _id
        get() = {
            if (field == null) println("Warning: got tx id, but tx id was unset. Was the transaction submitted?")
            field
        }()

    enum class State(val value: Int) {
        TX_REFUSED(-1),
        TX_NOT_YET_SENT(0),
        TX_NOT_YET_COMMITTED(1),
        TX_ACCEPTED(2),
    }

    fun submit() : Transaction {
        if (state != State.TX_NOT_YET_SENT) throw Exception("Transaction.submit() should only be called on" +
                "Transactions of state TX_NOT_YET_SENT")
        state = State.TX_NOT_YET_COMMITTED
        state = try {
            resolver?.invoke(this)
            State.TX_ACCEPTED
        } catch (e : Exception) {
            // todo: this should get some data
            State.TX_REFUSED
        }
        return this
    }

    fun detailsToMessageData() : MessageData {
        // TODO: use stdtx lol
        val msg = MessageData()
//        msg.strings["txId"] = txId
//        msg.strings[Keys.otherProfileId] = addressOut
//        msg.strings[Keys.coinsIn] = coinsIn.serialize()
//        msg.strings[Keys.coinsOut] = coinsOut.serialize()
//        msg.stringArrays[Keys.itemsIn] = itemsIn.serialize()
//        msg.stringArrays[Keys.itemsOut] = itemsOut.serialize()
        return msg
    }
}