package com.pylons.wallet.core.ops

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Coin.Companion.toCoinList
import java.io.StringReader

fun Core.sendCoins (coins : String, receiver : String) : Transaction =
        engine.sendCoins(
                Coin.listFromJson(klaxon.parseJsonArray(StringReader(coins)) as JsonArray<JsonObject>),
                receiver).submit()