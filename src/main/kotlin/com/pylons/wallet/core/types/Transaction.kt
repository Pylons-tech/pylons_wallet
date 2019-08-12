package com.pylons.wallet.core.types

import com.jayway.jsonpath.JsonPath
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import java.lang.NullPointerException

/**
 * Models a transaction. Internally, transactions are just sets of inputs and outputs
 */
data class Transaction(
        val requester : String? = null,
        val msg : Message? = null,
        val msgType : String? = null,
        val resolver : ((Transaction) -> Unit)? = null,
        var state: State = State.TX_NOT_YET_SENT
) {
    var id : String? = null
        get() = {
            if (field == null) println("Warning: got tx id, but tx id was unset. Was the transaction submitted?")
            field
        }()



    abstract class Message

    data class MsgGetPylons(val amount : Long = 0): Message() {
        companion object {
            fun fromResponse (json : String) : MsgGetPylons {
                val amount = JsonPath.read<Long>(json, "$.tx.msg.value.Amount.amount")
                return MsgGetPylons(amount)
            }
        }
    }

    data class MsgSendPylons(val amount : Long = 0, val sender : String = "", val receiver : String = ""): Message() {
        companion object {
            fun fromResponse (json : String) : MsgSendPylons {
                val amount = JsonPath.read<Long>(json, "$.tx.msg.value.Amount.amount")
                val sender = JsonPath.read<String>(json, "\$.tx.msg.value.Sender")
                val receiver = JsonPath.read<String>(json, "\$.tx.msg.value.Receiver")
                return MsgSendPylons(amount)
            }
        }
    }

    enum class State(val value: Int) {
        TX_REFUSED(-1),
        TX_NOT_YET_SENT(0),
        TX_NOT_YET_COMMITTED(1),
        TX_ACCEPTED(2),
    }

    companion object {
        fun build (txDescription: TransactionDescription) : Transaction? {
            TODO("????????")
//            return try {
//                val itemsIn = mutableListOf<Item>()
//                val itemsOut = mutableListOf<Item>()
//                txDescription.itemsInIds.forEach {
//                    itemsIn.add(Item.findInLocalProfile(it)!!)
//                }
//                txDescription.itemsOutIds.forEach {
//                    itemsOut.add(Item.findInBufferedForeignProfile(txDescription.otherProfileId, it)!!)
//                }
//                Transaction(Core.engine.getNewTransactionId(), Core.userProfile!!.credentials.address,
//                        txDescription.otherProfileId, txDescription.coinsIn, txDescription.coinsOut,
//                        itemsIn.toList(), itemsOut.toList())
//            } catch (e : NullPointerException) {
//                return null
//            }
        }


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