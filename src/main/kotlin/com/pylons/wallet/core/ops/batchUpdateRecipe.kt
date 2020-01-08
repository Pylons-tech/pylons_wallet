package com.pylons.wallet.core.ops

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.tx.recipe.CoinInput
import com.pylons.wallet.core.types.tx.recipe.ItemInput
import com.pylons.wallet.core.types.tx.recipe.ItemUpgradeParams
import com.pylons.wallet.core.types.tx.recipe.WeightedParamList

internal fun batchUpdateRecipe (msg: MessageData) : Response {
    checkValid(msg)
    val txs = Core.batchUpdateRecipe(
            ids = msg.stringArrays[Keys.ID]!!,
            names = msg.stringArrays[Keys.NAME]!!,
            cookbooks = msg.stringArrays[Keys.COOKBOOK]!!,
            descriptions = msg.stringArrays[Keys.DESCRIPTION]!!,
            blockIntervals = msg.longArrays[Keys.BLOCK_INTERVAL]!!.toList(),
            coinInputs = msg.stringArrays[Keys.COIN_INPUTS]!!,
            itemInputs = msg.stringArrays[Keys.ITEM_INPUTS]!!,
            outputTables = msg.stringArrays[Keys.OUTPUT_TABLES]!!
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
    require(msg.stringArrays.containsKey(Keys.ID)) { throw BadMessageException("batchUpdateRecipe", Keys.COOKBOOK, "StringArray") }
    require(msg.stringArrays.containsKey(Keys.COOKBOOK)) { throw BadMessageException("batchUpdateRecipe", Keys.COOKBOOK, "StringArray") }
    require(msg.stringArrays.containsKey(Keys.NAME)) { throw BadMessageException("batchUpdateRecipe", Keys.NAME, "StringArray") }
    require(msg.stringArrays.containsKey(Keys.DESCRIPTION)) { throw BadMessageException("batchUpdateRecipe", Keys.DESCRIPTION, "StringArray") }
    require(msg.longArrays.containsKey(Keys.BLOCK_INTERVAL)) { throw BadMessageException("batchUpdateRecipe", Keys.BLOCK_INTERVAL, "LongArray") }
    require(msg.stringArrays.containsKey(Keys.COIN_INPUTS)) { throw BadMessageException("batchUpdateRecipe", Keys.COIN_INPUTS, "StringArray") }
    require(msg.stringArrays.containsKey(Keys.ITEM_INPUTS)) { throw BadMessageException("batchUpdateRecipe", Keys.ITEM_INPUTS, "StringArray") }
    require(msg.stringArrays.containsKey(Keys.OUTPUT_TABLES)) { throw BadMessageException("batchUpdateRecipe", Keys.OUTPUT_TABLES, "StringArray") }
}

fun Core.batchUpdateRecipe (ids : List<String>, names : List<String>, cookbooks : List<String>, descriptions : List<String>,
                            blockIntervals : List<Long>, coinInputs: List<String>, itemInputs : List<String>,
                            outputTables : List<String>) : List<Transaction> {
    val mItemInputs = mutableListOf<List<ItemInput>>()
    itemInputs.forEach { mItemInputs.add(ItemInput.listFromJson(klaxon.parse<JsonArray<JsonObject>>(it))) }
    val mCoinInputs = mutableListOf<List<CoinInput>>()
    coinInputs.forEach { mCoinInputs.add(CoinInput.listFromJson(klaxon.parse<JsonArray<JsonObject>>(it))) }
    val mOutputTables = mutableListOf<WeightedParamList>()
    outputTables.forEach { mOutputTables.add(WeightedParamList.fromJson(klaxon.parse<JsonObject>(it))!!) }
    val txs = engine.updateRecipes(
            sender = userProfile!!.credentials.address,
            ids = ids,
            names = names,
            cookbookIds = cookbooks,
            descriptions = descriptions,
            blockIntervals = blockIntervals,
            coinInputs = mCoinInputs,
            itemInputs = mItemInputs,
            entries = mOutputTables
    ).toMutableList()
    txs.indices.forEach { txs[it] = txs[it].submit() }
    return txs
}