package com.pylons.wallet.core.ops

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.tx.recipe.CoinInput
import com.pylons.wallet.core.types.tx.recipe.ItemInput
import com.pylons.wallet.core.types.tx.recipe.WeightedParamList

internal fun updateRecipe (msg: MessageData) : Response {
    checkValid(msg)
    val tx = Core.updateRecipe(
            id = msg.strings[Keys.ID]!!,
            name = msg.strings[Keys.NAME]!!,
            cookbook = msg.strings[Keys.COOKBOOK]!!,
            description = msg.strings[Keys.DESCRIPTION]!!,
            blockInterval = msg.longs[Keys.BLOCK_INTERVAL]!!,
            coinInputs = msg.strings[Keys.COIN_INPUTS]!!,
            itemInputs = msg.strings[Keys.ITEM_INPUTS]!!,
            outputTables = msg.strings[Keys.OUTPUT_TABLES]!!
    )
    waitUntilCommitted(tx.id!!)
    val outgoingMessage = MessageData(
            strings = mutableMapOf(
                    Keys.TX to tx.id!!
            )
    )
    return Response(outgoingMessage, Status.OK_TO_RETURN_TO_CLIENT)
}

private fun checkValid (msg : MessageData) {
    require(msg.strings.containsKey(Keys.ID)) { throw BadMessageException("updateRecipe", Keys.ID, "String") }
    require(msg.strings.containsKey(Keys.COOKBOOK)) { throw BadMessageException("updateRecipe", Keys.COOKBOOK, "String") }
    require(msg.strings.containsKey(Keys.NAME)) { throw BadMessageException("updateRecipe", Keys.NAME, "String") }
    require(msg.strings.containsKey(Keys.DESCRIPTION)) { throw BadMessageException("updateRecipe", Keys.DESCRIPTION, "String") }
    require(msg.longs.containsKey(Keys.BLOCK_INTERVAL)) { throw BadMessageException("updateRecipe", Keys.BLOCK_INTERVAL, "Long") }
    require(msg.strings.containsKey(Keys.COIN_INPUTS)) { throw BadMessageException("updateRecipe", Keys.COIN_INPUTS, "String") }
    require(msg.strings.containsKey(Keys.ITEM_INPUTS)) { throw BadMessageException("updateRecipe", Keys.ITEM_INPUTS, "String") }
    require(msg.strings.containsKey(Keys.OUTPUT_TABLES)) { throw BadMessageException("updateRecipe", Keys.OUTPUT_TABLES, "String") }
}

fun Core.updateRecipe (id : String, name : String, cookbook : String, description : String, blockInterval : Long,
                       coinInputs : String, itemInputs : String, outputTables : String) : Transaction {
    val mItemInputs = ItemInput.listFromJson(klaxon.parse<JsonArray<JsonObject>>(itemInputs))
    val mCoinInputs = CoinInput.listFromJson(klaxon.parse<JsonArray<JsonObject>>(coinInputs))
    val mOutputTables = WeightedParamList.fromJson(klaxon.parse<JsonObject>(outputTables))!!
    return engine.updateRecipe(
            sender = userProfile!!.credentials.address,
            name = name,
            cookbookId = cookbook,
            description = description,
            blockInterval = blockInterval,
            coinInputs = mCoinInputs,
            itemInputs = mItemInputs,
            entries = mOutputTables,
            id = id
    )
}