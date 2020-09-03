package com.pylons.wallet.core.ops

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Transaction.Companion.submitAll
import com.pylons.wallet.core.types.tx.recipe.CoinInput
import com.pylons.wallet.core.types.tx.recipe.ItemInput
import com.pylons.wallet.core.types.tx.recipe.EntriesList
import com.pylons.wallet.core.types.tx.recipe.WeightedOutput

fun Core.batchUpdateRecipe (ids : List<String>, names : List<String>, cookbooks : List<String>, descriptions : List<String>,
                            blockIntervals : List<Long>, coinInputs: List<String>, itemInputs : List<String>,
                            outputTables : List<String>, outputs : List<String>) : List<Transaction> {
    val mItemInputs = mutableListOf<List<ItemInput>>()
    itemInputs.forEach { mItemInputs.add(ItemInput.listFromJson(klaxon.parse<JsonArray<JsonObject>>(it))) }
    val mCoinInputs = mutableListOf<List<CoinInput>>()
    coinInputs.forEach { mCoinInputs.add(CoinInput.listFromJson(klaxon.parse<JsonArray<JsonObject>>(it))) }
    val mOutputTables = mutableListOf<EntriesList>()
    outputTables.forEach { mOutputTables.add(EntriesList.fromJson(klaxon.parse<JsonObject>(it))!!) }
    val mOutputs = mutableListOf<List<WeightedOutput>>()
    outputs.forEach { mOutputs.add(WeightedOutput.listFromJson(klaxon.parse<JsonArray<JsonObject>>(it))) }
    val txs = engine.updateRecipes(
            ids = ids,
            names = names,
            cookbookIds = cookbooks,
            descriptions = descriptions,
            blockIntervals = blockIntervals,
            coinInputs = mCoinInputs,
            itemInputs = mItemInputs,
            entries = mOutputTables,
            outputs = mOutputs
    ).toMutableList()
    return txs.submitAll()
}