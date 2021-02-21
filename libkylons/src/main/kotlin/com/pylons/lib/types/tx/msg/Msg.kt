 package com.pylons.wallet.core.types.tx.msg

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Json
import com.beust.klaxon.Parser
import com.pylons.lib.types.tx.Coin
import com.pylons.lib.types.tx.item.Item
import com.pylons.lib.types.tx.recipe.*
import com.pylons.lib.types.tx.trade.TradeItemInput
import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.full.*

private annotation class MsgParser
private annotation class MsgType (
        val serializedAs : String
)

@ExperimentalUnsignedTypes
sealed class Msg() {

    companion object {
        fun fromJson (json : String) : Msg? = fromJson(Parser.default().parse(json) as JsonObject)

        fun fromJson (jsonObject: JsonObject) : Msg? {
            val identifier = jsonObject["type"] as String
            val msgType = findMsgType(identifier)
                    ?:
                throw Exception("No type matches message type $identifier")
            val parser = findParser(msgType) ?:
                throw Exception("No parser defined for message type $identifier")
            return parser(jsonObject.obj("value")!!)
        }

        private fun findMsgType(type : String) : KClass<out Msg>? {
            Msg::class.sealedSubclasses.forEach {
                val msgType = it.findAnnotation<MsgType>()
                if (msgType?.serializedAs == type) return it
            }
            return null
        }

        private fun findParser (type: KClass<out Msg>) : ((JsonObject) -> Msg?)? {
            type.companionObject?.declaredFunctions?.forEach {
                if (it.findAnnotation<MsgParser>() != null) {
                    return { o : JsonObject ->  it.call(type.companionObjectInstance, o) as Msg? }
                }
            }
            return null
        }
    }
}

 @MsgType("pylons/CheckExecution")
 data class CheckExecution (
         @property:[Json(name = "ExecID")]
         val execId : String,
         @property:[Json(name = "Sender")]
         val sender : String,
         @property:[Json(name = "PayToComplete")]
         val payToComplete : Boolean
 ) : Msg() {

     companion object {
         @MsgParser
         fun parse (jsonObject: JsonObject) : CheckExecution {
             return CheckExecution(
                     execId = jsonObject.string("ExecID")!!,
                     sender = jsonObject.string("Sender")!!,
                     payToComplete = jsonObject.boolean("PayToComplete")!!
             )
         }
     }
 }

 @MsgType("pylons/CreateAccount")
 data class CreateAccount(
         @property:[Json(name = "Requester")]
         val sender : String
 ) : Msg() {

     companion object {
         @MsgParser
         fun parse (jsonObject: JsonObject) : CreateAccount {
             return CreateAccount(
                     sender = jsonObject.string("Requester")!!
             )
         }
     }
 }

@MsgType("pylons/CreateCookbook")
data class CreateCookbook (
        @property:[Json(name = "CookbookID")]
        val cookbookId : String,
        @property:[Json(name = "Name")]
        val name : String,
        @property:[Json(name = "Description")]
        val description : String,
        @property:[Json(name = "Developer")]
        val developer : String,
        @property:[Json(name = "Version")]
        val version : String,
        @property:[Json(name = "SupportEmail")]
        val supportEmail : String,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "Level")]
        val level : Long,
        @property:[Json(name = "CostPerBlock")]
        val costPerBlock : Long
): Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : CreateCookbook {
            println(jsonObject.toJsonString())
            return CreateCookbook(
                    cookbookId = jsonObject.string("CookbookID")!!,
                    name = jsonObject.string("Name")!!,
                    description = jsonObject.string("Description")!!,
                    developer = jsonObject.string("Developer")!!,
                    version = jsonObject.string("Version")!!,
                    supportEmail = jsonObject.string("SupportEmail")!!,
                    sender = jsonObject.string("Sender")!!,
                    level = jsonObject.string("Level")!!.toLong(),
                    costPerBlock = jsonObject.string("CostPerBlock")!!.toLong()
            )
        }
    }
}

@MsgType("pylons/CreateRecipe")
data class CreateRecipe (
    @property:[Json(name = "BlockInterval")]
        val blockInterval : Long,
    @property:[Json(name = "CoinInputs")]
        val coinInputs : List<CoinInput>,
    @property:[Json(name = "CookbookID")]
        val cookbookId : String,
    @property:[Json(name = "Description")]
        val description: String,
    @property:[Json(name = "Entries")]
        val entries : EntriesList,
    @property:[Json(name = "Outputs")]
        val outputs : List<WeightedOutput>,
    @property:[Json(name = "ItemInputs")]
        val itemInputs : List<ItemInput>,
    @property:[Json(name = "Name")]
        val name : String,
    @property:[Json(name = "Sender")]
        val sender : String


):Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : CreateRecipe {
            return CreateRecipe(
                    name = jsonObject.string("Name")!!,
                    description = jsonObject.string("Description")!!,
                    cookbookId = jsonObject.string("CookbookID")!!,
                    sender = jsonObject.string("Sender")!!,
                    blockInterval = jsonObject.string("BlockInterval")!!.toLong(),
                    coinInputs = CoinInput.listFromJson(jsonObject.array("CoinInputs")),
                    itemInputs = ItemInput.listFromJson(jsonObject.array("ItemInputs")),
                    entries = EntriesList.fromJson(jsonObject.obj("Entries"))?:
                        EntriesList(listOf(), listOf(), listOf()),
                    outputs = WeightedOutput.listFromJson(jsonObject.array("Outputs"))

            )
        }
    }
}

@MsgType("pylons/CreateTrade")
data class CreateTrade (
    @property:[Json(name = "CoinInputs")]
        val coinInputs : List<CoinInput>,
    @property:[Json(name = "CoinOutputs") EmptyArray]
        val coinOutputs : List<Coin>,
    @property:[Json(name = "ExtraInfo")]
        val extraInfo : String,
    @property:[Json(name = "ItemInputs")]
        val itemInputs: List<TradeItemInput>,
    @property:[Json(name = "ItemOutputs")]
        val itemOutputs: List<Item>,
    @property:[Json(name = "Sender")]
        val sender : String
):Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : CreateTrade {
            return CreateTrade(
                    sender = jsonObject.string("Sender")!!,
                    extraInfo = jsonObject.string("ExtraInfo")!!,
                    coinInputs = CoinInput.listFromJson(jsonObject.array("CoinInputs")),
                    coinOutputs = Coin.listFromJson(jsonObject.array("CoinOutputs")),
                    itemInputs = TradeItemInput.listFromJson(jsonObject.array("ItemInputs")),
                    itemOutputs = Item.listFromJson(jsonObject.array("ItemOutputs"))

            )
        }
    }
}

@MsgType("pylons/DisableRecipe")
data class DisableRecipe(
        @property:[Json(name = "RecipeID")]
        val recipeId : String,
        @property:[Json(name="Sender")]
        val sender : String
) : Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : DisableRecipe {
            return DisableRecipe(
                    recipeId = jsonObject.string("RecipeID")!!,
                    sender = jsonObject.string("Sender")!!
            )
        }
    }
}

@MsgType("pylons/EnableRecipe")
data class EnableRecipe(
        @property:[Json(name = "RecipeID")]
        val recipeId : String,
        @property:[Json(name="Sender")]
        val sender : String
) : Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : EnableRecipe {
            return EnableRecipe(
                    recipeId = jsonObject.string("RecipeID")!!,
                    sender = jsonObject.string("Sender")!!
            )
        }
    }
}

@MsgType("pylons/ExecuteRecipe")
data class ExecuteRecipe(
        @property:[Json(name = "RecipeID")]
        val recipeId : String,
        @property:[Json(name = "ItemIDs")]
        val itemIds : List<String>,
        @property:[Json(name = "Sender")]
        val sender : String
) : Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : ExecuteRecipe {
            return ExecuteRecipe(
                    recipeId = jsonObject.string("RecipeID")!!,
                    sender = jsonObject.string("Sender")!!,
                    itemIds = jsonObject.array("ItemIDs")?: listOf()
            )
        }
    }
}

@MsgType("pylons/FulfillTrade")
data class FulfillTrade (
        @property:[Json(name = "TradeID")]
        val tradeId : String,
        @property:[Json(name = "ItemIDs")]
        val itemIds : List<String>,
        @property:[Json(name = "Sender")]
        val sender : String
):Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : FulfillTrade {
            return FulfillTrade(
                    sender = jsonObject.string("Sender")!!,
                    tradeId = jsonObject.string("TradeID")!!,
                    itemIds = jsonObject.array("ItemIDs") ?: listOf()
            )
        }
    }
}

@MsgType("pylons/DisableTrade")
data class CancelTrade (
        @property:[Json(name = "TradeID")]
        val tradeId : String,
        @property:[Json(name = "Sender")]
        val sender : String
):Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : CancelTrade {
            return CancelTrade(
                    sender = jsonObject.string("Sender")!!,
                    tradeId = jsonObject.string("TradeID")!!
            )
        }
    }
}

@MsgType("pylons/GetPylons")
data class GetPylons(
        @property:[Json(name = "Amount")]
        val amount : List<Coin>,
        @property:[Json(name = "Requester")]
        val sender : String
) : Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : GetPylons {
            return GetPylons(
                    amount = Coin.listFromJson(jsonObject.array("Amount")!!),
                    sender = jsonObject.string("Requester")!!
            )
        }
    }
}

@MsgType("pylons/SendCoins")
data class SendCoins(
        @property:[Json(name = "Amount")]
        val amount : List<Coin>,
        @property:[Json(name = "Receiver")]
        val receiver : String,
        @property:[Json(name = "Sender")]
        val sender : String
) : Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : SendCoins {
            return SendCoins(
                    amount = Coin.listFromJson(jsonObject.array("Amount")!!),
                    receiver = jsonObject.string("Receiver")!!,
                    sender = jsonObject.string("Sender")!!
            )
        }
    }
}

@MsgType("pylons/UpdateCookbook")
data class UpdateCookbook(
        @property:[Json(name = "ID")]
        val id : String,
        @property:[Json(name = "Description")]
        val description : String,
        @property:[Json(name = "Developer")]
        val developer : String,
        @property:[Json(name = "Version")]
        val version : String,
        @property:[Json(name = "SupportEmail")]
        val supportEmail : String,
        @property:[Json(name = "Sender")]
        val sender : String
): Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : UpdateCookbook {
            return UpdateCookbook(
                    id = jsonObject.string("ID")!!,
                    description = jsonObject.string("Description")!!,
                    developer = jsonObject.string("Developer")!!,
                    version = jsonObject.string("Version")!!,
                    supportEmail = jsonObject.string("SupportEmail")!!,
                    sender = jsonObject.string("Sender")!!
            )
        }
    }
}

@MsgType("pylons/UpdateItemString")
data class UpdateItemString (
        @property:[Json(name = "Field")]
        val field : String,
        @property:[Json(name = "ItemID")]
        val itemId : String,
        @property:[Json(name = "Value")]
        val value : String,
        @property:[Json(name = "Sender")]
        val sender: String
): Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : UpdateItemString {
            return UpdateItemString(
                    itemId = jsonObject.string("ItemID")!!,
                    field = jsonObject.string("Field")!!,
                    value = jsonObject.string("Value")!!,
                    sender = jsonObject.string("Sender")!!
            )
        }
    }
}

@MsgType("pylons/UpdateRecipe")
data class UpdateRecipe (
    @property:[Json(name = "BlockInterval")]
        val blockInterval : Long,
    @property:[Json(name = "CoinInputs")]
        val coinInputs : List<CoinInput>,
    @property:[Json(name = "CookbookID")]
        val cookbookId : String,
    @property:[Json(name = "Description")]
        val description: String,
    @property:[Json(name = "Entries")]
        val entries : EntriesList,
    @property:[Json(name = "Outputs")]
        val outputs : List<WeightedOutput>,
    @property:[Json(name = "ID")]
        val id : String,
    @property:[Json(name = "ItemInputs")]
        val itemInputs : List<ItemInput>,
    @property:[Json(name = "Name")]
        val name : String,
    @property:[Json(name = "Sender")]
        val sender : String
): Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : UpdateRecipe {
            return UpdateRecipe(
                    id = jsonObject.string("ID")!!,
                    name = jsonObject.string("Name")!!,
                    description = jsonObject.string("Description")!!,
                    cookbookId = jsonObject.string("CookbookID")!!,
                    sender = jsonObject.string("Sender")!!,
                    blockInterval = jsonObject.string("BlockInterval")!!.toLong(),
                    coinInputs = CoinInput.listFromJson(jsonObject.array("CoinInputs")),
                    itemInputs = ItemInput.listFromJson(jsonObject.array("ItemInputs")),
                    entries = EntriesList.fromJson(jsonObject.obj("Entries"))?:
                            EntriesList(listOf(), listOf(), listOf()),
                    outputs = WeightedOutput.listFromJson(jsonObject.array("Outputs"))
            )
        }
    }
}

@MsgType("pylons/SendItems")
data class SendItems(
        @property:[Json(name = "Receiver")]
        val receiver : String,
        @property:[Json(name = "ItemIDs")]
        val itemIds : List<String>,
        @property:[Json(name = "Sender")]
        val sender : String
) : Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : SendItems {
            return SendItems(
                    sender = jsonObject.string("Sender")!!,
                    receiver = jsonObject.string("Receiver")!!,
                    itemIds = jsonObject.array("ItemIDs") ?: listOf()
            )
        }
    }
}

@MsgType("pylons/GoogleIAPGetPylons")
data class GoogleIapGetPylons(
        @property:[Json(name = "ProductID")]
        val productId : String,
        @property:[Json(name = "PurchaseToken")]
        val purchaseToken : String,
        @property:[Json(name = "ReceiptDataBase64")]
        val receiptDataBase64 : String,
        @property:[Json(name = "Signature")]
        val signature : String,
        @property:[Json(name = "Requester")]
        val sender : String
) : Msg() {

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : GoogleIapGetPylons {
            return GoogleIapGetPylons(
                    productId = jsonObject.string("ProductID")!!,
                    purchaseToken = jsonObject.string("PurchaseToken")!!,
                    receiptDataBase64 = jsonObject.string("ReceiptDataBase64")!!,
                    signature = jsonObject.string("Signature")!!,
                    sender = jsonObject.string("Requester")!!
            )
        }
    }
}