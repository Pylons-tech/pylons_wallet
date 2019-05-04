package walletcore.types

import walletcore.constants.Keys
import java.lang.NullPointerException

data class TransactionDescription (
        val otherProfileId : String = "",
        val coinsIn: List<Coin> = listOf(),
        val coinsOut: List<Coin> = listOf(),
        val itemsInIds:List<String> = listOf(),
        val itemsOutIds:List<String> = listOf()
) {
    companion object {
        fun fromMessageData (msg : MessageData) : TransactionDescription? {
            try {
                val otherProfileId = msg.strings[Keys.otherProfileId]!!
                val coinsInCsv = msg.strings[Keys.coinsIn]
                val coinsIn = mutableListOf<Coin>()
                if (coinsInCsv != null && coinsInCsv != "") {
                    val split = coinsInCsv.split(",")
                    for (i in 0 until split.count()) if (i % 2 == 0) coinsIn.add(Coin(split[i], split[i+1].toInt()))
                }
                val coinsOutCsv = msg.strings[Keys.coinsOut]
                val coinsOut = mutableListOf<Coin>()
                if (coinsOutCsv != null && coinsOutCsv != "") {
                    val split = coinsOutCsv.split(",")
                    for (i in 0 until split.count()) if (i % 2 == 0) coinsOut.add(Coin(split[i], split[i+1].toInt()))
                }
                // TODO: fetch full item descriptions from the ids
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
                //val itemsCatalystsCsv = msg.strings["itemsCatalyst"]
                return TransactionDescription(otherProfileId, coinsIn.toList(), coinsOut.toList(),
                        itemsIn.toList(), itemsOut.toList())
            } catch (e : NullPointerException) {
                return null
            }
        }
    }
}