package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.tx.recipe.CoinInput
import com.pylons.wallet.core.types.tx.recipe.ItemInput
import com.pylons.wallet.core.types.tx.recipe.ItemUpgradeParams
import com.pylons.wallet.core.types.tx.recipe.WeightedParamList

internal fun createRecipe (msg: MessageData) : Response {
    checkValid(msg)
    val tx = Core.createRecipe(
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
    require(msg.strings.containsKey(Keys.COOKBOOK)) { throw BadMessageException("createRecipe", Keys.COOKBOOK, "String") }
    require(msg.strings.containsKey(Keys.NAME)) { throw BadMessageException("createRecipe", Keys.NAME, "String") }
    require(msg.strings.containsKey(Keys.DESCRIPTION)) { throw BadMessageException("createRecipe", Keys.DESCRIPTION, "String") }
    require(msg.longs.containsKey(Keys.BLOCK_INTERVAL)) { throw BadMessageException("createRecipe", Keys.BLOCK_INTERVAL, "Long") }
    require(msg.strings.containsKey(Keys.COIN_INPUTS)) { throw BadMessageException("createRecipe", Keys.COIN_INPUTS, "String") }
    require(msg.strings.containsKey(Keys.ITEM_INPUTS)) { throw BadMessageException("createRecipe", Keys.ITEM_INPUTS, "String") }
    require(msg.strings.containsKey(Keys.OUTPUT_TABLES)) { throw BadMessageException("createRecipe", Keys.OUTPUT_TABLES, "String") }
}

fun Core.createRecipe (name : String, cookbook : String, description : String, blockInterval : Long,
                       coinInputs : String, itemInputs : String, outputTables : String) : Transaction {
    val mCoinInputs = moshi.adapter<List<CoinInput>>(
            Types.newParameterizedType(List::class.java, CoinInput::class.java)).fromJson(coinInputs)!!
    val mItemInputs = moshi.adapter<List<ItemInput>>(
            Types.newParameterizedType(List::class.java, ItemInput::class.java)).fromJson(itemInputs)!!
    val mOutputTables = moshi.adapter<WeightedParamList>(
            WeightedParamList::class.java).fromJson(outputTables)!!
    return engine.createRecipe(
            sender = userProfile!!.credentials.address,
            name = name,
            cookbookId = cookbook,
            description = description,
            blockInterval = blockInterval,
            coinInputs = mCoinInputs,
            itemInputs = mItemInputs,
            entries = mOutputTables,
            // TODO: implement these properly
            rType = 0,
            toUpgrade = ItemUpgradeParams(listOf(), listOf(), listOf())
    )
}