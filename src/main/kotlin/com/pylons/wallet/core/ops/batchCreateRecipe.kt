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

internal fun batchCreateRecipe (msg: MessageData) : Response {
    checkValid(msg)
    val txs = Core.batchCreateRecipe(
            names = msg.stringArrays[Keys.NAME]!!,
            cookbooks = msg.stringArrays[Keys.COOKBOOK]!!,
            descriptions = msg.stringArrays[Keys.DESCRIPTION]!!,
            blockIntervals = msg.longArrays[Keys.BLOCK_INTERVAL]!!.toList(),
            coinInputs = msg.stringArrays[Keys.COIN_INPUTS]!!,
            itemInputs = msg.stringArrays[Keys.ITEM_INPUTS]!!,
            outputTables = msg.stringArrays[Keys.OUTPUT_TABLES]!!,
            rTypes = msg.longArrays[Keys.RECIPE_TYPE]!!.toList(),
            upgradeParams = msg.stringArrays[Keys.UPGRADE_PARAM]!!
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
    require(msg.stringArrays.containsKey(Keys.COOKBOOK)) { throw BadMessageException("batchCreateRecipe", Keys.COOKBOOK, "StringArray") }
    require(msg.stringArrays.containsKey(Keys.NAME)) { throw BadMessageException("batchCreateRecipe", Keys.NAME, "StringArray") }
    require(msg.stringArrays.containsKey(Keys.DESCRIPTION)) { throw BadMessageException("batchCreateRecipe", Keys.DESCRIPTION, "StringArray") }
    require(msg.longArrays.containsKey(Keys.BLOCK_INTERVAL)) { throw BadMessageException("batchCreateRecipe", Keys.BLOCK_INTERVAL, "LongArray") }
    require(msg.stringArrays.containsKey(Keys.COIN_INPUTS)) { throw BadMessageException("batchCreateRecipe", Keys.COIN_INPUTS, "StringArray") }
    require(msg.stringArrays.containsKey(Keys.ITEM_INPUTS)) { throw BadMessageException("batchCreateRecipe", Keys.ITEM_INPUTS, "StringArray") }
    require(msg.stringArrays.containsKey(Keys.OUTPUT_TABLES)) { throw BadMessageException("batchCreateRecipe", Keys.OUTPUT_TABLES, "StringArray") }
    require(msg.longArrays.containsKey(Keys.RECIPE_TYPE)) { throw BadMessageException("batchCreateRecipe", Keys.RECIPE_TYPE, "LongArray") }
    require(msg.stringArrays.containsKey(Keys.UPGRADE_PARAM)) { throw BadMessageException("batchCreateRecipe", Keys.UPGRADE_PARAM, "StringArray") }
}

fun Core.batchCreateRecipe (names : List<String>, cookbooks : List<String>, descriptions : List<String>,
                            blockIntervals : List<Long>, coinInputs: List<String>, itemInputs : List<String>,
                            outputTables : List<String>, rTypes : List<Long>, upgradeParams: List<String>) : List<Transaction> {
    val mItemInputs = mutableListOf<List<ItemInput>>()
    itemInputs.forEach { mItemInputs.add(ItemInput.listFromJson(klaxon.parse<JsonArray<JsonObject>>(it))) }
    val mCoinInputs = mutableListOf<List<CoinInput>>()
    coinInputs.forEach { mCoinInputs.add(CoinInput.listFromJson(klaxon.parse<JsonArray<JsonObject>>(it))) }
    val mOutputTables = mutableListOf<WeightedParamList>()
    outputTables.forEach { mOutputTables.add(WeightedParamList.fromJson(klaxon.parse<JsonObject>(it))!!) }
    val mUpgradeParams = mutableListOf<ItemUpgradeParams>()
    upgradeParams.forEach { mUpgradeParams.add(ItemUpgradeParams.fromJson(klaxon.parse<JsonObject>(it)!!)) }
    val txs =  engine.createRecipes(
            sender = userProfile!!.credentials.address,
            names = names,
            cookbookIds = cookbooks,
            descriptions = descriptions,
            blockIntervals = blockIntervals,
            coinInputs = mCoinInputs,
            itemInputs = mItemInputs,
            entries = mOutputTables,
            rTypes = rTypes,
            toUpgrade = mUpgradeParams
    ).toMutableList()
    txs.indices.forEach { txs[it] = txs[it].submit() }
    return txs
}