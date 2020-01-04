package com.pylons.wallet.core.types.tx.msg

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Json
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.types.jsonTemplate.baseJsonWeldFlow
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.tx.recipe.*
import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.full.*

private annotation class MsgParser
private annotation class MsgType (
        val serializedAs : String
)

sealed class Msg {
    abstract fun serializeForIpc() : String

    companion object {
        fun fromJson (jsonObject: JsonObject) : Msg? {
            val identifier = jsonObject["type"] as String
            val msgType = findMsgType(identifier)
                    ?:
                throw Exception("No type matches message type $identifier")
            val parser = findParser(msgType) ?:
                throw Exception("No parser defined for message type $identifier")
            return parser(jsonObject)
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

@MsgType("pylons/CreateCookbook")
data class CreateCookbook(
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
            return CreateCookbook(
                    name = jsonObject.string("Name")!!,
                    description = jsonObject.string("Description")!!,
                    developer = jsonObject.string("Developer")!!,
                    version = jsonObject.string("Version")!!,
                    supportEmail = jsonObject.string("SupportEmail")!!,
                    sender = jsonObject.string("Sender")!!,
                    level = jsonObject.long("level")!!,
                    costPerBlock = jsonObject.long("CostPerBlock")!!
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
        val entries : WeightedParamList,
        @property:[Json(name = "ItemInputs")]
        val itemInputs : List<ItemInput>,
        @property:[Json(name = "Name")]
        val name : String,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "RType")]
        val rType : Long,
        @property:[Json(name = "ToUpgrade")]
        val toUpgrade : ItemUpgradeParams
):Msg() {
    private class CreateRecipeAdapter(val mode: SerializationMode = SerializationMode.FOR_BROADCAST) : JsonAdapter<CreateRecipe>() {
        override fun fromJson(p0: JsonReader): CreateRecipe? =
                throw NotImplementedError("This adapter does not support deserialization operations")

        @ToJson
        override fun toJson(p0: JsonWriter, p1: CreateRecipe?) = JsonModelSerializer.serialize(mode, p0, p1)
    }

    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        private val msgAdapter = CreateRecipeAdapter(SerializationMode.FOR_BROADCAST)
        private val signingAdapter = CreateRecipeAdapter(SerializationMode.FOR_SIGNING)

        @MsgParser
        fun parse (jsonObject: JsonObject) : CreateRecipe {
            return CreateRecipe(
                    name = jsonObject.string("Name")!!,
                    description = jsonObject.string("Description")!!,
                    cookbookId = jsonObject.string("CookbookID")!!,
                    sender = jsonObject.string("Sender")!!,
                    rType = jsonObject.long("RType")!!,
                    blockInterval = jsonObject.long("BlockInterval")!!,
                    coinInputs = CoinInput.listFromJson(jsonObject.array("CoinInputs")!!),
                    itemInputs = ItemInput.listFromJson(jsonObject.array("ItemInputs")!!),
                    entries = WeightedParamList.fromJson(jsonObject.obj("Entries"))?:
                        WeightedParamList(listOf(), listOf()),
                    toUpgrade = ItemUpgradeParams.fromJson(jsonObject.obj("ToUpgrade")!!)
            )
        }
    }

    private fun toMsgJson () : String = """
        [
        {
            "type": "pylons/CreateRecipe",
            "value": ${msgAdapter.toJson(this)}
        }
        ]"""

    fun toSignedTx () : String {
        val c = Core.userProfile!!.credentials as TxPylonsEngine.Credentials
        val crypto = (Core.engine as TxPylonsEngine).cryptoHandler
        return baseJsonWeldFlow(toMsgJson(), toSignStruct(), c.accountNumber, c.sequence, crypto.keyPair!!.publicKey())
    }

    fun toSignStruct () : String = "[${signingAdapter.toJson(this)}]"
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
                    itemIds = jsonObject.array("ItemIDs")!!
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
        val amount : Long,
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
                    amount = jsonObject.long("Amount")!!,
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
        val entries : WeightedParamList,
        @property:[Json(name = "ID")]
        val id : String,
        @property:[Json(name = "ItemInputs")]
        val itemInputs : List<ItemInput>,
        @property:[Json(name = "Name")]
        val name : String,
        @property:[Json(name = "Sender")]
        val sender : String
): Msg() {
    class UpdateRecipeAdapter(val mode: SerializationMode = SerializationMode.FOR_BROADCAST) : JsonAdapter<UpdateRecipe>() {
        @FromJson
        override fun fromJson(p0: JsonReader): UpdateRecipe? =
                throw NotImplementedError("This adapter does not support deserialization operations")

        @ToJson
        override fun toJson(p0: JsonWriter, p1: UpdateRecipe?) = JsonModelSerializer.serialize(mode, p0, p1)
    }

    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        val msgAdapter = UpdateRecipeAdapter(SerializationMode.FOR_BROADCAST)
        val signingAdapter = UpdateRecipeAdapter(SerializationMode.FOR_SIGNING)

        @MsgParser
        fun parse (jsonObject: JsonObject) : UpdateRecipe {
            return UpdateRecipe(
                    id = jsonObject.string("ID")!!,
                    name = jsonObject.string("Name")!!,
                    description = jsonObject.string("Description")!!,
                    cookbookId = jsonObject.string("CookbookID")!!,
                    sender = jsonObject.string("Sender")!!,
                    blockInterval = jsonObject.long("BlockInterval")!!,
                    coinInputs = CoinInput.listFromJson(jsonObject.array("CoinInputs")!!),
                    itemInputs = ItemInput.listFromJson(jsonObject.array("ItemInputs")!!),
                    entries = WeightedParamList.fromJson(jsonObject.obj("Entries")!!)?:
                            WeightedParamList(listOf(), listOf())
            )
        }
    }

    private fun toMsgJson () : String = """
        [
        {
            "type": "pylons/UpdateRecipe",
            "value": ${msgAdapter.toJson(this)}
        }
        ]"""

    fun toSignedTx () : String {
        val c = Core.userProfile!!.credentials as TxPylonsEngine.Credentials
        val crypto = (Core.engine as TxPylonsEngine).cryptoHandler
        return baseJsonWeldFlow(toMsgJson(), toSignStruct(), c.accountNumber, c.sequence, crypto.keyPair!!.publicKey())
    }

    fun toSignStruct () : String = "[${signingAdapter.toJson(this)}]"
}

