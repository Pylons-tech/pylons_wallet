package tech.pylons.lib.types.tx

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import tech.pylons.lib.internal.fuzzyLong
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.CoinInput
import tech.pylons.lib.types.tx.recipe.ItemInput
import tech.pylons.lib.types.tx.trade.ItemRef
import tech.pylons.lib.types.tx.trade.TradeItemInput
import java.lang.StringBuilder

data class Trade(
    @property:[Json(name = "creator")]
        val Creator : String,
    @property:[Json(name = "ID")]
        val ID : Long,
    @property:[Json(name = "coinInputs")]
        val CoinInputs : List<CoinInput>,
    @property:[Json(name = "itemInputs")]
        val ItemInputs : List<ItemInput>,
    @property:[Json(name = "coinOutputs")]
        val CoinOutputs : List<Coin>,
    @property:[Json(name = "itemOutputs")]
        val ItemOutputs: List<ItemRef>,
    @property:[Json(name = "extraInfo")]
        val ExtraInfo : String,
    @property:[Json(name = "receiver")]
        val Receiver : String,
    @property:[Json(name = "tradedItemInputs")]
        val TradedItemInputs : List<ItemRef>

) {
    companion object {
        fun fromJson(json: String): Trade? {
            val jsonObj = Parser.default().parse(StringBuilder(json)) as JsonObject
            if (jsonObj != null){
                return Trade(
                    Creator = jsonObj.string("creator")!!,
                    ID = jsonObj.fuzzyLong("ID") ?: 0,
                    CoinInputs = CoinInput.listFromJson(jsonObj.array("coinInputs")),
                    ItemInputs = ItemInput.listFromJson(jsonObj.array("itemInputs")),
                    CoinOutputs = Coin.listFromJson(jsonObj.array("coinOutputs")),
                    ItemOutputs = ItemRef.listFromJson(jsonObj.array("itemOutputs")),
                    ExtraInfo = jsonObj.string("extraInfo").orEmpty(),
                    Receiver = jsonObj.string("receiver")!!,
                    TradedItemInputs = ItemRef.listFromJson(jsonObj.array("tradedItemInputs"))
                )

            }
            return null
        }

        fun fromObj(jsonObj: JsonObject): Trade? {
            return Trade(
                Creator =  jsonObj.string("creator")!!,
                ID = jsonObj.fuzzyLong("ID") ?: 0,
                CoinInputs = CoinInput.listFromJson(jsonObj.array("coinInputs")),
                ItemInputs = ItemInput.listFromJson(jsonObj.array("itemInputs")),
                CoinOutputs = Coin.listFromJson(jsonObj.array("coinOutputs")),
                ItemOutputs = ItemRef.listFromJson(jsonObj.array("itemOutputs")),
                ExtraInfo = jsonObj.string("extraInfo").orEmpty(),
                Receiver = jsonObj.string("receiver")!!,
                TradedItemInputs = ItemRef.listFromJson(jsonObj.array("tradedItemInputs"))
            )
            return null
        }

        fun listFromJson (json : String) : List<Trade> {
            val jsonArray = (Parser.default().parse(StringBuilder(json)) as JsonObject).array<JsonObject>("Trades").orEmpty()
            val list = mutableListOf<Trade>()
            jsonArray.forEach {
                list.add(
                    Trade(
                        Creator = it.string("creator")!!,
                        ID = it.fuzzyLong("ID") ?: 0,
                        CoinInputs = CoinInput.listFromJson(it.array("coinInputs")),
                        ItemInputs = ItemInput.listFromJson(it.array("itemInputs")),
                        CoinOutputs = Coin.listFromJson(it.array("coinOutputs")),
                        ItemOutputs = ItemRef.listFromJson(it.array("itemOutputs")),
                        ExtraInfo = it.string("extraInfo").orEmpty(),
                        Receiver = it.string("receiver")!!,
                        TradedItemInputs = ItemRef.listFromJson(it.array("tradedItemInputs"))
                    )
                )
            }
            return list
        }
    }
}