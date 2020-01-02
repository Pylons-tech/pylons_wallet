package com.pylons.wallet.core.types.tx.msg

import com.jayway.jsonpath.JsonPath
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.types.jsonTemplate.baseJsonWeldFlow
import com.pylons.wallet.core.types.tx.recipe.*
import com.squareup.moshi.*
import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.full.*


sealed class Msg {
    abstract fun serializeForIpc() : String
    protected val moshi = Moshi.Builder().build()

    companion object {

        fun fromJson (json : String) : Msg? {
            val identifier = JsonPath.read<String>("$.type", json)
            val msgType = findMsgType(identifier)
                    ?:
                throw Exception("No type matches message type $identifier")
            val parser = findParser(msgType) ?:
                throw Exception("No parser defined for message type $identifier")
            return parser(json)
        }

        private fun findMsgType(type : String) : KClass<out Msg>? {
            Msg::class.sealedSubclasses.forEach {
                val msgType = it.findAnnotation<MsgType>()
                if (msgType?.serializedAs == type) return it
            }
            return null
        }

        private fun findParser (type: KClass<out Msg>) : ((String) -> Msg?)? {
            type.companionObject?.declaredFunctions?.forEach {
                if (it.findAnnotation<MsgParser>() != null) {
                    return { s : String ->  it.call(s) as Msg? }
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
    override fun serializeForIpc(): String = moshi.adapter<CreateCookbook>(CreateCookbook::class.java).toJson(this)

    companion object {
        @MsgParser
        fun parse (json : String) : CreateCookbook {
            return CreateCookbook(
                    name = JsonPath.read<String>(json, "$.Name"),
                    description = JsonPath.read<String>(json, "$.Description"),
                    developer = JsonPath.read<String>(json, "$.Developer"),
                    version = JsonPath.read<String>(json, "$.Version"),
                    supportEmail = JsonPath.read<String>(json, "$.SupportEmail"),
                    sender = JsonPath.read<String>(json, "$.Sender"),
                    level = JsonPath.read<Long>(json, "$.Level"),
                    costPerBlock = JsonPath.read<Long>(json, "$.CostPerBlock")
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
    override fun serializeForIpc(): String = moshi.adapter<CreateRecipe>(CreateRecipe::class.java).toJson(this)

    companion object {
        val msgAdapter = CreateRecipeAdapter(SerializationMode.FOR_BROADCAST)
        val signingAdapter = CreateRecipeAdapter(SerializationMode.FOR_SIGNING)

        @MsgParser
        fun parse (json : String) : CreateRecipe {
            return CreateRecipe(
                    name = JsonPath.read<String>(json, "$.Name"),
                    description = JsonPath.read<String>(json, "$.Description"),
                    cookbookId = JsonPath.read<String>(json, "$.CookbookID"),
                    sender = JsonPath.read<String>(json, "$.Sender"),
                    rType = JsonPath.read<Long>(json, "$.RType"),
                    blockInterval = JsonPath.read<Long>(json, "$.BlockInterval"),
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

data class ExecuteRecipe(
        @property:[Json(name = "RecipeID")]
        val recipeId : String,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "ItemIDs")]
        val itemIds : List<String>?
) : Msg()

class CreateRecipeAdapter(val mode: SerializationMode = SerializationMode.FOR_BROADCAST) : JsonAdapter<CreateRecipe>() {
    override fun fromJson(p0: JsonReader): CreateRecipe? =
            throw NotImplementedError("This adapter does not support deserialization operations")

    @ToJson
    override fun toJson(p0: JsonWriter, p1: CreateRecipe?) = JsonModelSerializer.serialize(mode, p0, p1)
}

