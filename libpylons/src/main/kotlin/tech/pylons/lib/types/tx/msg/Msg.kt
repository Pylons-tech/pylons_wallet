package tech.pylons.lib.types.tx.msg

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Json
import com.beust.klaxon.Parser
import tech.pylons.lib.*
import tech.pylons.lib.core.ICore
import tech.pylons.lib.types.credentials.CosmosCredentials
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.lib.types.tx.trade.TradeItemInput
import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.full.*

private annotation class MsgParser
private annotation class MsgType (
        val serializedAs : String
)

private annotation class MsgResType(
    val serializedAs: String
)

@ExperimentalUnsignedTypes
sealed class Msg() {
    abstract fun serializeForIpc() : String

    companion object {
        private var core : ICore? = null
        fun useCore(core : ICore) {
            Companion.core = core
        }

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
                var msgType = it.findAnnotation<MsgType>()
                if (msgType?.serializedAs == type) return it

                var msgResType = it.findAnnotation<MsgResType>()
                if (msgResType?.serializedAs == type) return it

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

    private fun toMsgJson () : String {
        val msgType = this::class.annotations.find { it is MsgType } as? MsgType
        // tierre: protobuf change
        var msg = JsonModelSerializer.serialize(SerializationMode.FOR_BROADCAST, this)
        if (msg != "null") {
            msg = msg.substring(1, msg.length-1) //remove outer brackets: {}
        }
        return """
            [
            {
                "@type": "${msgType?.serializedAs.orEmpty()}",
                ${msg}
            }
            ]"""
    }

    //Tierre modify here for signed Tx
    fun toSignedTx () : String {
        val c = core!!.userProfile!!.credentials as CosmosCredentials
        val crypto = core!!.engine.cryptoHandler
        return core!!.buildJsonForTxPost(toMsgJson(), toSignStruct(), c.accountNumber, c.sequence, crypto.keyPair!!.publicKey(), 400000)
    }

    fun toSignStruct () : String = "[${JsonModelSerializer.serialize(SerializationMode.FOR_SIGNING, this)}]"
}
//wonder this type is correct in new server protobuf
 @MsgType("/pylons.MsgCheckExecution")
 @MsgResType("pylons/CheckExecution")
 data class CheckExecution (
         @property:[Json(name = "ExecID")]
         val execId : String,
         @property:[Json(name = "Sender")]
         val sender : String,
         @property:[Json(name = "PayToComplete")]
         val payToComplete : Boolean
 ) : Msg() {
     override fun serializeForIpc(): String = klaxon.toJsonString(this)

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

 @MsgType("/pylons.MsgCreateAccount")
 @MsgResType("pylons/CreateAccount")
 data class CreateAccount(
         @property:[Json(name = "Requester")]
         val sender : String
 ) : Msg() {
     override fun serializeForIpc(): String = klaxon.toJsonString(this)

     companion object {
         @MsgParser
         fun parse (jsonObject: JsonObject) : CreateAccount {
             return CreateAccount(
                     sender = jsonObject.string("Requester")!!
             )
         }
     }
 }

@MsgType("/pylons.MsgCreateCookbook")
@MsgResType("pylons/CreateCookbook")
data class CreateCookbook (
        @property:[Json(name = "CookbookID")]
        val cookbookId : String,
        @property:[Json(name = "Name")]
        val name : String,
        @property:[Json(name = "Description")]
        val description : String,
        @property:[Json(name = "Version")]
        val version : String,
        @property:[Json(name = "Developer")]
        val developer : String,
        @property:[Json(name = "SupportEmail")]
        val supportEmail : String,
        @property:[Json(name = "Level")]
        val level : Long,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "CostPerBlock")]
        val costPerBlock : Long
): Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

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

@MsgType("/pylons.MsgCreateRecipe")
@MsgResType("pylons/CreateRecipe")
data class CreateRecipe (
    //optional RecipeID if someone - new server protobuf
    @property:[Json(name = "RecipeID")]
    val recipeId: String,
    @property:[Json(name = "Name")]
    val name : String,
    @property:[Json(name = "CookbookID")]
    val cookbookId : String,
    @property:[Json(name = "CoinInputs")]
    val coinInputs : List<CoinInput>,
    @property:[Json(name = "ItemInputs")]
    val itemInputs : List<ItemInput>,
    @property:[Json(name = "Outputs")]
    val outputs : List<WeightedOutput>,
    @property:[Json(name = "BlockInterval")]
        val blockInterval : Long,
    @property:[Json(name = "Sender")]
    val sender : String,
    @property:[Json(name = "Description")]
        val description: String,
    @property:[Json(name = "Entries")]
        val entries : EntriesList,
    @property:[Json(name = "ExtraInfo")] //newly added field
        val extraInfo: String


): Msg() {

    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser

        fun parse (jsonObject: JsonObject) : CreateRecipe {
            var blockInterval:Long = 0
            if (jsonObject.containsKey("BlockInterval")){
               blockInterval =  jsonObject.string("BlockInterval")!!.toLong()
            }
            return CreateRecipe(
                    recipeId=jsonObject.string("RecipeID").orEmpty(),
                    name = jsonObject.string("Name").orEmpty(),
                    description = jsonObject.string("Description").orEmpty(),
                    cookbookId = jsonObject.string("CookbookID").orEmpty(),
                    sender = jsonObject.string("Sender").orEmpty(),
                    blockInterval = blockInterval,
                    coinInputs = CoinInput.listFromJson(jsonObject.array("CoinInputs")),
                    itemInputs = ItemInput.listFromJson(jsonObject.array("ItemInputs")),
                    entries = EntriesList.fromJson(jsonObject.obj("Entries"))?:
                        EntriesList(listOf(), listOf(), listOf()),
                    outputs = WeightedOutput.listFromJson(jsonObject.array("Outputs")),
                    extraInfo = jsonObject.string("ExtraInfo").orEmpty()

            )
        }
    }
}

@MsgType("/pylons.MsgCreateTrade")
@MsgResType("pylons/CreateTrade")
data class CreateTrade (
    @property:[Json(name = "CoinInputs")]
        val coinInputs : List<CoinInput>,
    @property:[Json(name = "ItemInputs")]
        val itemInputs: List<TradeItemInput>,
    @property:[Json(name = "CoinOutputs") EmptyArray]
        val coinOutputs : List<Coin>,
    @property:[Json(name = "ItemOutputs")]
    val itemOutputs: List<Item>,
    @property:[Json(name = "ExtraInfo")]
        val extraInfo : String,
    @property:[Json(name = "Sender")]
        val sender : String
): Msg() {

    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : CreateTrade {
            return CreateTrade(
                    sender = jsonObject.string("Sender")!!,
                    extraInfo = jsonObject.string("ExtraInfo") ?: "",
                    coinInputs = CoinInput.listFromJson(jsonObject.array("CoinInputs")),
                    coinOutputs = Coin.listFromJson(jsonObject.array("CoinOutputs")),
                    itemInputs = TradeItemInput.listFromJson(jsonObject.array("ItemInputs")),
                    itemOutputs = Item.listFromJson(jsonObject.array("ItemOutputs"))

            )
        }
    }
}

@MsgType("/pylons.MsgDisableRecipe")
@MsgResType("pylons/DisableRecipe")
data class DisableRecipe(
        @property:[Json(name = "RecipeID")]
        val recipeId : String,
        @property:[Json(name="Sender")]
        val sender : String
) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

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

@MsgType("/pylons.MsgEnableRecipe")
@MsgResType("pylons/EnableRecipe")
data class EnableRecipe(
        @property:[Json(name = "RecipeID")]
        val recipeId : String,
        @property:[Json(name="Sender")]
        val sender : String
) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

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

@MsgType("/pylons.MsgExecuteRecipe")
@MsgResType("pylons/ExecuteRecipe")
data class ExecuteRecipe(
        @property:[Json(name = "RecipeID")]
        val recipeId : String,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "ItemIDs")]
        val itemIds : List<String>,
        @property:[Json(name = "PaymentId")]
        val paymentId: String = ""
) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : ExecuteRecipe {
            return ExecuteRecipe(
                    recipeId = jsonObject.string("RecipeID")!!,
                    sender = jsonObject.string("Sender")!!,
                    itemIds = jsonObject.array("ItemIDs")?: listOf(),
                    paymentId = jsonObject.string("PaymentId")?: ""
            )
        }
    }
}

@MsgType("/pylons.MsgEnableTrade")
@MsgResType("pylons/EnableTrade")
data class EnableTrade(
    @property:[Json(name = "TradeID")]
    val tradeId : String,
    @property:[Json(name = "Sender")]
    val sender : String
) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : EnableTrade {
            return EnableTrade(
                tradeId = jsonObject.string("TradeID")!!,
                sender = jsonObject.string("Sender")!!
            )
        }
    }
}

@MsgType("/pylons.MsgFiatItem")
@MsgResType("pylons/FiatItem")
data class FiatItem(
    @property:[Json(name = "CookbookID")]
    val cookbookId : String,
    @property:[Json(name = "Doubles") QuotedJsonNumeral]
    val doubles: Map<String, Double>,
    @property:[Json(name = "Longs") QuotedJsonNumeral]
    val longs: Map<String, Long>,
    @property:[Json(name = "Strings")]
    val strings: Map<String, String>,
    @property:[Json(name = "Sender")]
    val sender : String,
    @property:[Json(name = "TransferFee")]
    val transferFee: Long
) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : FiatItem {
            val jsonDoubleArray = jsonObject.array<JsonObject>("Doubles")
            val mmDoubles = mutableMapOf<String, Double>()
            if (jsonDoubleArray != null) {
                jsonDoubleArray.forEach { mmDoubles[it.string("Key")!!] = it.string("Value")!!.toDouble() }
            }

            val jsonLongArray = jsonObject.array<JsonObject>("Longs")
            val mmLongs = mutableMapOf<String, Long>()
            if (jsonLongArray != null) {
                jsonLongArray.forEach { mmLongs[it.string("Key")!!] = it.string("Value")!!.toLong() }
            }

            val jsonStringArray = jsonObject.array<JsonObject>("Strings")
            val mmStrings = mutableMapOf<String, String>()
            if (jsonStringArray != null) {
                jsonStringArray.forEach { mmStrings[it.string("Key")!!] = it.string("Value")!!.toString() }
            }


            return FiatItem(
                cookbookId = jsonObject.string("CookbookID")!!,
                doubles = mmDoubles,
                longs = mmLongs,
                strings = mmStrings,
                sender = jsonObject.string("Sender")!!,
                transferFee = jsonObject.long("TransferFee")!!
            )
        }
    }
}

@MsgType("/pylons.MsgFulfillTrade")
@MsgResType("pylons/FulfillTrade")
data class FulfillTrade (
        @property:[Json(name = "TradeID")]
        val tradeId : String,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "ItemIDs")]
        val itemIds : List<String>
): Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

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

@MsgType("/pylons.MsgGetPylons")
@MsgResType("pylons/GetPylons")
data class GetPylons(
    @property:[Json(name = "Amount")]
    val amount : List<Coin>,
    @property:[Json(name = "Requester")]
    val sender : String
) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

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

@MsgType("/pylons.MsgDisableTrade")
@MsgResType("pylons/DisableTrade")
data class CancelTrade (
        @property:[Json(name = "TradeID")]
        val tradeId : String,
        @property:[Json(name = "Sender")]
        val sender : String
): Msg() {

    override fun serializeForIpc(): String = klaxon.toJsonString(this)

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


@MsgType("/pylons.MsgSendCoins")
@MsgResType("pylons/SendCoins")
data class SendCoins(
        @property:[Json(name = "Amount")]
        val amount : List<Coin>,
        @property:[Json(name = "Receiver")]
        val receiver : String,
        @property:[Json(name = "Sender")]
        val sender : String
) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

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

@MsgType("/pylons.MsgUpdateCookbook")
@MsgResType("pylons/UpdateCookbook")
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
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

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

@MsgType("/pylons.MsgUpdateItemString")
@MsgResType("pylons/UpdateItemString")
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
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

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

@MsgType("/pylons.MsgUpdateRecipe")
@MsgResType("pylons/UpdateRecipe")
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
        val sender : String,
    @property:[Json(name = "ExtraInfo")]
        val extraInfo: String
): Msg() {

    override fun serializeForIpc(): String = klaxon.toJsonString(this)

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
                    outputs = WeightedOutput.listFromJson(jsonObject.array("Outputs")),
                    extraInfo = jsonObject.string("ExtraInfo")!!
            )
        }
    }
}

@MsgType("/pylons.MsgSendItems")
@MsgResType("pylons/SendItems")
data class SendItems(
        @property:[Json(name = "Receiver")]
        val receiver : String,
        @property:[Json(name = "ItemIDs")]
        val itemIds : List<String>,
        @property:[Json(name = "Sender")]
        val sender : String
) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

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

@MsgType("/pylons.MsgGoogleIAPGetPylons")
@MsgResType("pylons/GoogleIAPGetPylons")
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
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

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