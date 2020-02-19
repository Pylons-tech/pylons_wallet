package com.pylons.wallet.core.types.tx.msg

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
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

@MsgType("pylons/CreateCookbook")
data class CreateCookbook (
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

    override fun serializeForIpc(): String = klaxon.toJsonString(this)

    companion object {
        @MsgParser
        fun parse (jsonObject: JsonObject) : CreateRecipe {
            return CreateRecipe(
                    name = jsonObject.string("Name")!!,
                    description = jsonObject.string("Description")!!,
                    cookbookId = jsonObject.string("CookbookID")!!,
                    sender = jsonObject.string("Sender")!!,
                    rType = jsonObject.string("RType")!!.toLong(),
                    blockInterval = jsonObject.string("BlockInterval")!!.toLong(),
                    coinInputs = CoinInput.listFromJson(jsonObject.array("CoinInputs")),
                    itemInputs = ItemInput.listFromJson(jsonObject.array("ItemInputs")),
                    entries = WeightedParamList.fromJson(jsonObject.obj("Entries"))?:
                        WeightedParamList(listOf(), listOf()),
                    toUpgrade = ItemUpgradeParams.fromJson(jsonObject.obj("ToUpgrade")!!)
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
                    entries = WeightedParamList.fromJson(jsonObject.obj("Entries"))?:
                            WeightedParamList(listOf(), listOf())
            )
        }
    }
}
