package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun batchDisableRecipe (msg: MessageData) : Response {
    checkValid(msg)
    val txs = Core.batchDisableRecipe(
            recipes = msg.stringArrays[Keys.RECIPE]!!
    )
    val txList = mutableListOf<String>()
    txs.forEach {
        waitUntilCommitted(it.id!!)
        txList.add(it.id!!)
    }
    val outgoingMessage = MessageData(
            stringArrays = mutableMapOf(
                    Keys.TX to txList
            )
    )
    return Response(outgoingMessage, Status.OK_TO_RETURN_TO_CLIENT)
}

private fun checkValid (msg : MessageData) {
    require(msg.strings.containsKey(Keys.RECIPE)) { throw BadMessageException("batchDisableRecipe", Keys.RECIPE, "String") }
}

fun Core.batchDisableRecipe (recipes : List<String>) : List<Transaction> {
    val txs = engine.disableRecipes(
            recipes = recipes
    ).toMutableList()
    txs.indices.forEach { txs[it] = txs[it].submit() }
    return txs
}