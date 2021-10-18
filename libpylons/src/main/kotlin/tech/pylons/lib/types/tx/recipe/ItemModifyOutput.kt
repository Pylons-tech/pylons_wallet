package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.NeverQuoteWrap
import tech.pylons.lib.types.tx.Coin

data class ItemModifyOutput(
    @property:[Json(name = "ID")]
        val ID: String,
    @property:[Json(name = "itemInputRef") NeverQuoteWrap]
        val itemInputRef: String,
    @property:[Json(name = "doubles")]
        val doubles: List<DoubleParam>,
    @property:[Json(name = "longs")]
        val longs: List<LongParam>,
    @property:[Json(name = "strings")]
        val strings: List<StringParam>,
    @property:[Json(name = "mutableStrings")]
        val mutableStrings: List<StringKeyValue>,
    @property:[Json(name = "TransferFee")]
        val transferFee: List<Coin>,
    @property:[Json(name = "tradePercentage")]
        val tradePercentage : String,
    @property:[Json(name = "quantity")]
        val quantity : Long,
    @property:[Json(name = "amountMinted")]
        val amountMinted : Long,
    @property:[Json(name = "tradeable")]
        val tradeable : Boolean

    ) {
    companion object {
        fun fromJson(jsonObject: JsonObject): ItemModifyOutput =
                ItemModifyOutput(
                    ID = jsonObject.string("ID")!!,
                    itemInputRef = jsonObject.string("itemInputRef")!!,
                        doubles = DoubleParam.listFromJson(jsonObject.array("doubles")),
                        longs = LongParam.listFromJson(jsonObject.array("longs")),
                        strings = StringParam.listFromJson(jsonObject.array("strings")),
                        mutableStrings = StringKeyValue.listFromJson(jsonObject.array("mutableStrings")),
                        transferFee = Coin.listFromJson(jsonObject.array("transferFee")),
                        tradePercentage = jsonObject.string("tradePercentage")!!,
                        quantity = jsonObject.long("quantity") ?: 0,
                        amountMinted = jsonObject.long("amountMinted") ?: 0,
                        tradeable = jsonObject.boolean("tradeable") ?: false
                )

        fun listFromJson(jsonArray: JsonArray<JsonObject>?): List<ItemModifyOutput> {
            if (jsonArray == null) return listOf()
            val ls = mutableListOf<ItemModifyOutput>()
            jsonArray.forEach { ls.add(fromJson(it)) }
            return ls
        }
    }
}