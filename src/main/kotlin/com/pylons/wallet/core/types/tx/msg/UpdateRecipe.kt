package com.pylons.wallet.core.types.tx.msg

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.types.jsonTemplate.baseJsonWeldFlow
import com.pylons.wallet.core.types.tx.recipe.*
import com.squareup.moshi.*

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
    companion object {
        val msgAdapter = UpdateRecipeAdapter(SerializationMode.FOR_BROADCAST)
        val signingAdapter = UpdateRecipeAdapter(SerializationMode.FOR_SIGNING)
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

class UpdateRecipeAdapter(val mode: SerializationMode = SerializationMode.FOR_BROADCAST) : JsonAdapter<UpdateRecipe>() {
    @FromJson
    override fun fromJson(p0: JsonReader): UpdateRecipe? {
        throw NotImplementedError("This adapter does not support deserialization operations")
    }

    @ToJson
    override fun toJson(p0: JsonWriter, p1: UpdateRecipe?) {
        JsonModelSerializer.serialize(mode, p0, p1)
    }
}