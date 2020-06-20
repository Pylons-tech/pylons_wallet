package com.pylons.wallet.core.types.tx.msg

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Json
import com.beust.klaxon.Parser
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.types.jsonTemplate.baseJsonWeldFlow
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.tx.item.Item
import com.pylons.wallet.core.types.tx.recipe.*
import com.pylons.wallet.core.types.tx.trade.TradeItemInput
import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.full.*

private annotation class MsgParser
private annotation class MsgType (
        val serializedAs : String
)

@ExperimentalUnsignedTypes
sealed class Msg {
    abstract fun serializeForIpc() : String

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

    private fun toMsgJson () : String {
        val msgType = this::class.annotations.find { it is MsgType } as? MsgType
        return """
            [
            {
                "type": "${msgType?.serializedAs.orEmpty()}",
                "value": ${JsonModelSerializer.serialize(SerializationMode.FOR_BROADCAST, this)}
            }
            ]"""
    }

    fun toSignedTx () : String {
        val c = Core.userProfile!!.credentials as TxPylonsEngine.Credentials
        val crypto = (Core.engine as TxPylonsEngine).cryptoCosmos
        return baseJsonWeldFlow(toMsgJson(), toSignStruct(), c.accountNumber, c.sequence, crypto.keyPair!!.publicKey())
    }

    fun toSignStruct () : String = "[${JsonModelSerializer.serialize(SerializationMode.FOR_SIGNING, this)}]"
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

    override fun serializeForIpc(): String = klaxon.toJsonString(this)

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
                        EntriesList(listOf(), listOf()),
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
        val coinOutputs : List<CoinOutput>,
        @property:[Json(name = "ExtraInfo")]
        val extraInfo : String,
        @property:[Json(name = "ItemInputs")]
        val itemInputs: List<TradeItemInput>,
        @property:[Json(name = "ItemOutputs")]
        val itemOutputs: List<Item>,
        @property:[Json(name = "Sender")]
        val sender : String
):Msg() {

    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : CreateTrade {
            return CreateTrade(
                    sender = jsonObject.string("Sender")!!,
                    extraInfo = jsonObject.string("ExtraInfo")!!,
                    coinInputs = CoinInput.listFromJson(jsonObject.array("CoinInputs")),
                    coinOutputs = CoinOutput.listFromJson(jsonObject.array("CoinOutputs")),
                    itemInputs = TradeItemInput.listFromJson(jsonObject.array("ItemInputs")),
                    itemOutputs = Item.listFromJson(jsonObject.array("ItemOutputs"))

            )
        }
    }
}

@MsgType("pylons/DisableRecipe")
data class DisableRecipe(
        @property:[Json(name = "RecipeID")]
        val recipeId : String
) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : DisableRecipe {
            return DisableRecipe(
                    recipeId = jsonObject.string("RecipeID")!!
            )
        }
    }
}

@MsgType("pylons/EnableRecipe")
data class EnableRecipe(
        @property:[Json(name = "RecipeID")]
        val recipeId : String
) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : EnableRecipe {
            return EnableRecipe(
                    recipeId = jsonObject.string("RecipeID")!!
            )
        }
    }
}

@MsgType("pylons/ExecuteRecipe")
data class ExecuteRecipe(
        @property:[Json(name = "RecipeID")]
        val recipeId : String,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "ItemIDs")]
        val itemIds : List<String>?
) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : ExecuteRecipe {
            return ExecuteRecipe(
                    recipeId = jsonObject.string("RecipeID")!!,
                    sender = jsonObject.string("Sender")!!,
                    itemIds = jsonObject.array("ItemIDs")
            )
        }
    }
}

@MsgType("pylons/FulfillTrade")
data class FulfillTrade (
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "TradeID")]
        val tradeId : String,
        @property:[Json(name = "ItemIDs")]
        val itemIds : List<String>
):Msg() {

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

@MsgType("pylons/DisableTrade")
data class CancelTrade (
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "TradeID")]
        val tradeId : String
):Msg() {

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

@MsgType("pylons/GetPylons")
data class GetPylons(
        @property:[Json(name = "Amount")]
        val amount : List<Coin>,
        @property:[Json(name = "Requester")]
        val requester : String
) : Msg() {
    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : GetPylons {
            return GetPylons(
                    amount = Coin.listFromJson(jsonObject.array("Amount")!!),
                    requester = jsonObject.string("Requester")!!
            )
        }
    }
}

@MsgType("pylons/SendPylons")
data class SendPylons(
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
        fun parse (jsonObject: JsonObject) : SendPylons {
            return SendPylons(
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

@MsgType("pylons/UpdateItemString")
data class UpdateItemString (
        @property:[Json(name = "Field")]
        val field : String,
        @property:[Json(name = "ItemID")]
        val itemId : String,
        @property:[Json(name = "Sender")]
        val sender: String,
        @property:[Json(name = "Value")]
        val value : String
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
                            EntriesList(listOf(), listOf()),
                    outputs = WeightedOutput.listFromJson(jsonObject.array("Outputs"))
            )
        }
    }
}

