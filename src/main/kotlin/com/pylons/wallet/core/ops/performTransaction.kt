package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.bufferForeignProfile
import com.pylons.wallet.core.types.*

fun performTransaction (msg : MessageData) : Response {
    Core.foreignProfilesBuffer = setOf()
    if (!msg.strings.containsKey(Keys.OTHER_ADDRESS)) throw IllegalArgumentException("Did not provide an ID for remote profile involved in transaction.")
    if (bufferForeignProfile(msg.strings[Keys.OTHER_ADDRESS]!!) == null) throw Exception("No profile with provided ID exists.")
    val txDesc = TransactionDescription.fromMessageData(msg)
    return when (txDesc) {
        null -> throw Exception("Could not generate tx from supplied TX description")
        else -> {
            val tx = Transaction.build(txDesc) ?: throw Exception("Failed to build transaction from TransactionDescription. This shouldn't happen; debug it.")
            Core.engine.commitTx(tx)
            val msg = MessageData(booleans = mutableMapOf(Keys.SUCCESS to true))
            Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
        }
    }
}

fun Core.performTx (txDesc : TransactionDescription) : Transaction = engine.commitTx(Transaction.build(txDesc)!!)