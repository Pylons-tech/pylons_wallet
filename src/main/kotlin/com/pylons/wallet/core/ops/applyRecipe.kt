package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.*
import com.pylons.wallet.core.types.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

internal fun applyRecipe (msg: MessageData) : Response {
    checkValid(msg)
    val tx = Core.applyRecipe(msg.strings[Keys.RECIPE]!!, msg.strings[Keys.COINS_IN]!!)
    waitUntilCommitted(tx.id!!)
    return Response(MessageData(strings = mutableMapOf(Keys.TX to tx.id!!)), Status.OK_TO_RETURN_TO_CLIENT)
}

private fun checkValid (msg : MessageData) {
    require(msg.strings.containsKey(Keys.RECIPE)) { throw BadMessageException("applyRecipe", Keys.RECIPE, "String") }
    require(msg.strings.containsKey(Keys.COINS_IN)) { throw BadMessageException("applyRecipe", Keys.COINS_IN, "String") }
}

fun Core.applyRecipe (recipe : String, coinsIn : String) : Transaction {
    val moshi = Moshi.Builder().build()
    val type = Types.newParameterizedType(Map::class.java, String::class.java, Int::class.java)
    val adapter = moshi.adapter<Map<String, Long>>(type)
    val map = adapter.fromJson(coinsIn)!!
    return  engine.applyRecipe(recipe, map)
}