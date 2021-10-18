package tech.pylons.lib.types.tx

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.CoinInput
import tech.pylons.lib.types.tx.recipe.ItemInput
import tech.pylons.lib.types.tx.trade.ItemRef
import tech.pylons.lib.types.tx.trade.TradeItemInput
import java.lang.StringBuilder

data class Trade(
    @property:[Json(name = "Creator")]
        val Creator : String,
    @property:[Json(name = "ID")]
        val ID : String,
    @property:[Json(name = "CoinInputs")]
        val CoinInputs : List<CoinInput>,
    @property:[Json(name = "ItemInputs")]
        val ItemInputs : List<ItemInput>,
    @property:[Json(name = "CoinOutputs")]
        val CoinOutputs : List<Coin>,
    @property:[Json(name = "ItemOutputs")]
        val ItemOutputs: List<ItemRef>,
    @property:[Json(name = "ExtraInfo")]
        val ExtraInfo : String,
    @property:[Json(name = "Receiver")]
        val Receiver : String,
    @property:[Json(name = "TradedItemInputs")]
        val TradedItemInputs : List<ItemRef>

) {
    companion object {
        fun fromJson(json: String): Trade? {
            val jsonObj = Parser.default().parse(StringBuilder(json)) as JsonObject
            if (jsonObj != null){
                return Trade(
                    Creator = jsonObj.string("Creator")!!,
                    ID = jsonObj.string("ID")!!,
                    CoinInputs = CoinInput.listFromJson(jsonObj.array("CoinInputs")),
                    ItemInputs = ItemInput.listFromJson(jsonObj.array("ItemInputs")),
                    CoinOutputs = Coin.listFromJson(jsonObj.array("CoinOutputs")),
                    ItemOutputs = ItemRef.listFromJson(jsonObj.array("ItemOutputs")),
                    ExtraInfo = jsonObj.string("ExtraInfo").orEmpty(),
                    Receiver = jsonObj.string("Receiver")!!,
                    TradedItemInputs = ItemRef.listFromJson(jsonObj.array("TradedItemInputs"))
                )

            }
            return null
        }

        fun listFromJson (json : String) : List<Trade> {
            val jsonArray = (Parser.default().parse(StringBuilder(json)) as JsonObject).array<JsonObject>("trades").orEmpty()
            val list = mutableListOf<Trade>()
            jsonArray.forEach {
                list.add(
                    Trade(
                        Creator = it.string("creator")!!,
                        ID = it.string("ID")!!,
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