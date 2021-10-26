package tech.pylons.lib.types.tx.msg

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Json
import com.beust.klaxon.Parser
import tech.pylons.lib.*
import tech.pylons.lib.core.ICore
import tech.pylons.lib.internal.fuzzyLong
import tech.pylons.lib.types.credentials.CosmosCredentials
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.lib.types.tx.trade.ItemRef
import tech.pylons.lib.types.tx.trade.TradeItemInput
import java.io.StringReader
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
sealed class Msg {
    abstract fun serializeForIpc() : String

    companion object {
        private var core : ICore? = null
        fun useCore(core : ICore) {
            Companion.core = core
        }

        fun fromJson (json : String) : Msg? = fromJson(Parser.default().parse(StringReader(json)) as JsonObject)

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
                $msg
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

@MsgType("/Pylonstech.pylons.pylons.MsgCreateAccount")
 @MsgResType("pylons/CreateAccount")
 data class CreateAccount(
         @property:[Json(name = "creator")]
         val creator : String,
         @property:[Json(name = "username")]
         val username : String
 ) : Msg() {
     override fun serializeForIpc(): String = klaxon.toJsonString(this)

     companion object {
         @MsgParser
         fun parse (jsonObject: JsonObject) : CreateAccount {
             return CreateAccount(
                    creator = jsonObject.string("creator")!!,
                    username = jsonObject.string("username")!!
             )
         }
     }
 }

@MsgType("/Pylonstech.pylons.pylons.MsgCreateCookbook")
@MsgResType("pylons/CreateCookbook")
data class CreateCookbook (
        @property:[Json(name = "creator")]
        val creator : String,
        @property:[Json(name = "ID")]
        val ID : String,
        @property:[Json(name = "name")]
        val name : String,
        @property:[Json(name = "description")]
        val description : String,
        @property:[Json(name = "developer")]
        val developer : String,
        @property:[Json(name = "version")]
        val version : String,
        @property:[Json(name = "supportEmail")]
        val supportEmail : String,
        @property:[Json(name = "costPerBlock")]
        val costPerBlock : Coin,
        @property:[Json(name = "enabled")]
        val enabled : Boolean

): Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : CreateCookbook {
            println(jsonObject.toJsonString())
            return CreateCookbook(
                creator = jsonObject.string("creator")!!,
                    ID = jsonObject.string("ID")!!,
                    name = jsonObject.string("name")!!,
                    description = jsonObject.string("description")!!,
                    developer = jsonObject.string("developer")!!,
                    version = jsonObject.string("version")!!,
                    supportEmail = jsonObject.string("supportEmail")!!,
                    costPerBlock = Coin.fromJson(jsonObject.obj("costPerBlock")!!),
                    enabled = jsonObject.boolean("enabled") ?: false
            )
        }
    }
}

@MsgType("/Pylonstech.pylons.pylons.MsgCreateRecipe")
@MsgResType("pylons/CreateRecipe")
data class CreateRecipe (
    //optional RecipeID if someone - new server protobuf
    @property:[Json(name = "creator")]
    val creator: String,
    @property:[Json(name = "cookbookID")]
    val cookbookID: String,
    @property:[Json(name = "ID")]
    val ID: String,
    @property:[Json(name = "name")]
    val name : String,
    @property:[Json(name = "description")]
    val description : String,
    @property:[Json(name = "version")]
    val version : String,
    @property:[Json(name = "coinInputs")]
    val coinInputs : List<CoinInput>,
    @property:[Json(name = "itemInputs")]
    val itemInputs : List<ItemInput>,
    @property:[Json(name = "entries")]
    val entries : EntriesList,
    @property:[Json(name = "outputs")]
    val outputs : List<WeightedOutput>,
    @property:[Json(name = "blockInterval")]
        val blockInterval : Long,
    @property:[Json(name = "enabled")]
    val enabled : Boolean,
    @property:[Json(name = "extraInfo")] //newly added field
        val extraInfo: String


): Msg() {

    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser

        fun parse (jsonObject: JsonObject) : CreateRecipe {
            var blockInterval:Long = 0
            if (jsonObject.containsKey("blockInterval")){
               blockInterval =  jsonObject.string("blockInterval")!!.toLong()
            }
            return CreateRecipe(
                creator=jsonObject.string("creator").orEmpty(),
                cookbookID = jsonObject.string("cookbookID").orEmpty(),
                ID=jsonObject.string("ID").orEmpty(),
                name = jsonObject.string("name").orEmpty(),
                description = jsonObject.string("description").orEmpty(),
                version = jsonObject.string("version").orEmpty(),
                coinInputs = CoinInput.listFromJson(jsonObject.array("coinInputs")),
                itemInputs = ItemInput.listFromJson(jsonObject.array("itemInputs")),
                entries = EntriesList.fromJson(jsonObject.obj("entries"))?:EntriesList(listOf(), listOf(), listOf()),
                outputs = WeightedOutput.listFromJson(jsonObject.array("outputs")),
                blockInterval = blockInterval,
                enabled = jsonObject.boolean("enabled") ?: false,
                extraInfo = jsonObject.string("extraInfo").orEmpty()

            )
        }
    }
}

@MsgType("/Pylonstech.pylons.pylons.MsgCreateTrade")
@MsgResType("pylons/CreateTrade")
data class CreateTrade (
    @property:[Json(name = "creator")]
        val Creator : String,
    @property:[Json(name = "coinInputs")]
        val CoinInputs : List<CoinInput>,
    @property:[Json(name = "itemInputs")]
        val ItemInputs: List<ItemInput>,
    @property:[Json(name = "coinOutputs") EmptyArray]
        val CoinOutputs : List<Coin>,
    @property:[Json(name = "itemOutputs")]
        val ItemOutputs: List<ItemRef>,
    @property:[Json(name = "extraInfo")]
        val ExtraInfo : String
): Msg() {

    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : CreateTrade {
            return CreateTrade(
                Creator = jsonObject.string("creator")!!,
                CoinInputs = CoinInput.listFromJson(jsonObject.array("coinInputs")),
                ItemInputs = ItemInput.listFromJson(jsonObject.array("itemInputs")),
                CoinOutputs = Coin.listFromJson(jsonObject.array("coinOutputs")),
                ItemOutputs = ItemRef.listFromJson(jsonObject.array("itemOutputs")),
                ExtraInfo = jsonObject.string("extraInfo") ?: ""

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

@MsgType("/Pylonstech.pylons.pylons.MsgExecuteRecipe")
@MsgResType("pylons/ExecuteRecipe")
data class ExecuteRecipe(
    @property:[Json(name = "creator")]
        val Creator : String,
    @property:[Json(name = "cookbookID")]
        val CookbookID : String,
    @property:[Json(name = "recipeID")]
        val RecipeID : String,
    @property:[Json(name = "coinInputsIndex")]
        val CoinInputsIndex : Long,
    @property:[Json(name = "itemIDs")]
        val ItemIDs : List<String>,
    @property:[Json(name = "paymentInfos", ignored = true)]
        var paymentInfos : PaymentInfo? = null
) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : ExecuteRecipe {
            return ExecuteRecipe(
                Creator = jsonObject.string("creator")!!,
                CookbookID = jsonObject.string("cookbookID")!!,
                RecipeID = jsonObject.string("recipeID")!!,
                CoinInputsIndex = jsonObject.long("coinInputsIndex")?: 0,
                ItemIDs = jsonObject.array("itemIDs")?: listOf(),
                //this can be null
                //paymentInfos = PaymentInfo.fromJson(jsonObject.obj("paymentInfos")!!)
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
                transferFee = jsonObject.fuzzyLong("TransferFee")
            )
        }
    }
}

@MsgType("/Pylonstech.pylons.pylons.MsgFulfillTrade")
@MsgResType("pylons/FulfillTrade")
data class FulfillTrade (
    @property:[Json(name = "creator")]
        val Creator : String,
    @property:[Json(name = "ID")]
        val ID : Long,
    @property:[Json(name = "coinInputsIndex")]
        val CoinInputsIndex : Long,
    @property:[Json(name = "items")]
        val Items: List<ItemRef>,
    @property:[Json(name = "paymentInfos", ignored = true)]
        var paymentInfos : PaymentInfo? = null
): Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : FulfillTrade {
            return FulfillTrade(
                Creator = jsonObject.string("creator")!!,
                ID = jsonObject.long("ID") ?: 0,
                CoinInputsIndex = jsonObject.long("coinInputsIndex") ?: 0,
                Items = ItemRef.listFromJson(jsonObject.array("items")),
                //this can be null
                //paymentInfos = PaymentInfo.fromJson(jsonObject.obj("paymentInfos")!!)
            )
        }
    }
}

@MsgType("/Pylonstech.pylons.pylons.MsgCancelTrade")
@MsgResType("pylons/CancelTrade")
data class CancelTrade (
        @property:[Json(name = "creator")]
        val Creator : String,
        @property:[Json(name = "ID")]
        val ID : Long
): Msg() {

    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : CancelTrade {
            return CancelTrade(
                Creator = jsonObject.string("creator")!!,
                ID = jsonObject.string("ID")?.toLong() ?: 0

            )
        }
    }
}

@MsgType("/Pylonstech.pylons.pylons.MsgUpdateCookbook")
@MsgResType("pylons/UpdateCookbook")
data class UpdateCookbook(
    @property:[Json(name = "creator")]
        val Creator : String,
    @property:[Json(name = "ID")]
        val ID : String,
    @property:[Json(name = "name")]
        val Name : String,
    @property:[Json(name = "description")]
        val Description : String,
    @property:[Json(name = "developer")]
        val Developer : String,
    @property:[Json(name = "version")]
        val Version : String,
    @property:[Json(name = "supportEmail")]
        val SupportEmail : String,
    @property:[Json(name = "costPerBlock")]
        val CostPerBlock : Coin,
    @property:[Json(name = "enabled")]
        val Enabled : Boolean
): Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : UpdateCookbook {
            return UpdateCookbook(
                Creator = jsonObject.string("creator")!!,
                ID = jsonObject.string("ID")!!,
                Name = jsonObject.string("name")!!,
                Description = jsonObject.string("description")!!,
                Developer = jsonObject.string("developer")!!,
                Version = jsonObject.string("version")!!,
                SupportEmail = jsonObject.string("supportEmail")!!,
                CostPerBlock = Coin.fromJson(jsonObject.obj("costPerBlock")!!),
                Enabled = jsonObject.boolean("enabled")!!
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

@MsgType("/Pylonstech.pylons.pylons.MsgUpdateRecipe")
@MsgResType("pylons/UpdateRecipe")
data class UpdateRecipe (
    @property:[Json(name = "creator")]
        val Creator : String,
    @property:[Json(name = "cookbookID")]
        val CookbookID : String,
    @property:[Json(name = "ID")]
        val ID : String,
    @property:[Json(name = "name")]
        val Name : String,
    @property:[Json(name = "description")]
        val Description: String,
    @property:[Json(name = "version")]
        val Version: String,
    @property:[Json(name = "coinInputs")]
        val CoinInputs : List<CoinInput>,
    @property:[Json(name = "itemInputs")]
        val ItemInputs : List<ItemInput>,
    @property:[Json(name = "entries")]
        val Entries : EntriesList,
    @property:[Json(name = "outputs")]
        val Outputs : List<WeightedOutput>,
    @property:[Json(name = "blockInterval")]
        val BlockInterval : Long,
    @property:[Json(name = "enabled")]
        val Enabled : Boolean,
    @property:[Json(name = "extraInfo")]
        val ExtraInfo: String
): Msg() {

    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : UpdateRecipe {
            return UpdateRecipe(
                Creator = jsonObject.string("creator")!!,
                CookbookID = jsonObject.string("cookbookID")!!,
                ID = jsonObject.string("ID")!!,
                Name = jsonObject.string("name")!!,
                Description = jsonObject.string("description")!!,
                Version = jsonObject.string("version")!!,
                CoinInputs = CoinInput.listFromJson(jsonObject.array("coinInputs")),
                ItemInputs = ItemInput.listFromJson(jsonObject.array("itemInputs")),
                Entries = EntriesList.fromJson(jsonObject.obj("entries"))?:
                EntriesList(listOf(), listOf(), listOf()),
                Outputs = WeightedOutput.listFromJson(jsonObject.array("outputs")),
                BlockInterval = jsonObject.string("blockInterval")!!.toLong(),
                Enabled = jsonObject.boolean("enabled")!!,
                ExtraInfo = jsonObject.string("extraInfo")!!
            )
        }
    }
}

@MsgType("/Pylonstech.pylons.pylons.MsgSendItems")
@MsgResType("pylons/SendItems")
data class SendItems(
    @property:[Json(name = "Creator")]
        val Creator : String,
    @property:[Json(name = "Receiver")]
        val Receiver : String,
    @property:[Json(name = "Items")]
        val Items : List<String>,

) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : SendItems {
            return SendItems(
                Creator = jsonObject.string("Creator")!!,
                Receiver = jsonObject.string("Receiver")!!,
                Items = jsonObject.array("Items") ?: listOf()
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