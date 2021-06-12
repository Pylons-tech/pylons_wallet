package tech.pylons.lib.types.tx

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.CoinInput
import tech.pylons.lib.types.tx.trade.TradeItemInput
import java.lang.StringBuilder

data class Trade(
    @property:[Json(name = "NodeVersion")]
        val nodeVersion : String,
    @property:[Json(name = "ID")]
        val id : String,
    @property:[Json(name = "CoinInputs")]
        val coinInputs : List<CoinInput>,
    @property:[Json(name = "ItemInputs")]
        val itemInputs : List<TradeItemInput>,
    @property:[Json(name = "CoinOutputs")]
        val coinOutputs : List<Coin>,
    @property:[Json(name = "ItemOutputs")]
        val itemOutputs: List<Item>,
    @property:[Json(name = "ExtraInfo")]
        val extraInfo : String,
    @property:[Json(name = "Sender")]
        val sender : String,
    @property:[Json(name = "FulFiller")]
        val fulfiller : String,
    @property:[Json(name = "Disabled")]
        val disabled : Boolean,
    @property:[Json(name = "Completed")]
        val completed : Boolean
) {
    companion object {
        fun fromJson(json: String): Trade? {
            val jsonObj = Parser.default().parse(StringBuilder(json)) as JsonObject
            if (jsonObj != null){
                return Trade(
                    nodeVersion = jsonObj.string("NodeVersion")!!,
                    id = jsonObj.string("ID")!!,
                    coinInputs = CoinInput.listFromJson(jsonObj.array("CoinInputs")),
                    itemInputs = TradeItemInput.listFromJson(jsonObj.array("ItemInputs")),
                    coinOutputs = Coin.listFromJson(jsonObj.array("CoinOutputs")),
                    itemOutputs = Item.listFromJson(jsonObj.array("ItemOutputs")),
                    extraInfo = jsonObj.string("ExtraInfo").orEmpty(),
                    sender = jsonObj.string("Sender")!!,
                    fulfiller = jsonObj.string("FulFiller").orEmpty(),
                    disabled = jsonObj.boolean("Disabled")!!,
                    completed = jsonObj.boolean("Completed")!!
                )

            }
            return null
        }

        fun listFromJson (json : String) : List<Trade> {
            val jsonArray = (Parser.default().parse(StringBuilder(json)) as JsonObject)!!.array<JsonObject>("trades").orEmpty()
            val list = mutableListOf<Trade>()
            jsonArray.forEach {
                list.add(
                    Trade(
                        nodeVersion = it.string("NodeVersion")!!,
                        id = it.string("ID")!!,
                        coinInputs = CoinInput.listFromJson(it.array("CoinInputs")),
                        itemInputs = TradeItemInput.listFromJson(it.array("ItemInputs")),
                        coinOutputs = Coin.listFromJson(it.array("CoinOutputs")),
                        itemOutputs = Item.listFromJson(it.array("ItemOutputs")),
                        extraInfo = it.string("ExtraInfo").orEmpty(),
                        sender = it.string("Sender")!!,
                        fulfiller = it.string("FulFiller").orEmpty(),
                        disabled = it.boolean("Disabled")!!,
                        completed = it.boolean("Completed")!!)
                )
            }
            return list
        }
    }
}