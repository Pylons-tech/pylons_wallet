package walletcore.types

import walletcore.constants.Keys
import java.lang.NullPointerException

data class TransactionDescription (
        val otherProfileId : String = "",
        val coinsIn: Set<Coin> = setOf(),
        val coinsOut: Set<Coin> = setOf(),
        val itemsInIds:Set<String> = setOf(),
        val itemsOutIds:Set<String> = setOf()
) {
    companion object {
        fun fromMessageData (msg : MessageData) : TransactionDescription? {
            try {
                val otherProfileId = msg.strings[Keys.otherProfileId]!!
                val coinsInCsv = msg.strings[Keys.coinsIn]
                val coinsIn = mutableSetOf<Coin>()
                if (coinsInCsv != null && coinsInCsv != "") {
                    val split = coinsInCsv.split(",")
                    for (i in 0 until split.count()) if (i % 2 == 0) coinsIn.add(Coin(split[i], split[i+1].toInt()))
                }
                val coinsOutCsv = msg.strings[Keys.coinsOut]
                val coinsOut = mutableSetOf<Coin>()
                if (coinsOutCsv != null && coinsOutCsv != "") {
                    val split = coinsOutCsv.split(",")
                    for (i in 0 until split.count()) if (i % 2 == 0) coinsOut.add(Coin(split[i], split[i+1].toInt()))
                }
                // fetch full item descriptions from the ids
                val itemsOutCsv = msg.strings[Keys.itemsOut]
                val itemsOut = when (itemsOutCsv) {
                    null -> listOf()
                    else -> itemsOutCsv?.split(",")
                }
                val itemsInCsv = msg.strings[Keys.itemsIn]
                val itemsIn = when (itemsInCsv) {
                    null -> listOf()
                    else -> itemsInCsv?.split(",")
                }
                return TransactionDescription(otherProfileId, coinsIn.toSet(), coinsOut.toSet(), itemsIn.toSet(), itemsOut.toSet())
            } catch (e : NullPointerException) {
                return null
            }
        }
    }
}