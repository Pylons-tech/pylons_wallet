package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.*
import tech.pylons.lib.internal.fuzzyInt
import tech.pylons.lib.internal.fuzzyLong
import tech.pylons.lib.types.tx.Coin

data class PaymentInfo (
        @property:[Json(name = "purchaseID")]
        val purchaseID : String,
        @property:[Json(name = "processorName")]
        val processorName : String,
        @property:[Json(name = "payerAddr")]
        val payerAddr : String,
        @property:[Json(name = "amount")]
        val amount : Int,
        @property:[Json(name = "productID")]
        val productID : String,
        @property:[Json(name = "signature")]
        val signature : String,
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : PaymentInfo =
                        PaymentInfo (
                                purchaseID = jsonObject.string("purchaseID") ?: "",
                                processorName = jsonObject.string("processorName") ?: "",
                                payerAddr = jsonObject.string("payerAddr") ?: "",
                                amount = jsonObject.fuzzyInt("amount") ?: 0,
                                productID = jsonObject.string("productID") ?: "",
                                signature = jsonObject.string("signature") ?: ""
                        )

                fun listFromJson (jsonArray: JsonArray<JsonObject>?) : List<PaymentInfo> {
                        if (jsonArray == null) return listOf()
                        val ls = mutableListOf<PaymentInfo>()
                        jsonArray.forEach { ls.add(fromJson(it)) }
                        return ls
                }
        }
}