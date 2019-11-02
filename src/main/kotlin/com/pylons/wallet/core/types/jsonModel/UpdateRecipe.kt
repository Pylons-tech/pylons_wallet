package com.pylons.wallet.core.types.jsonModel

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.types.jsonTemplate.baseJsonWeldFlow
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
) {
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

    fun toSignStruct () : String {
        return "[${signingAdapter.toJson(this)}]"
    }
}

class UpdateRecipeAdapter(val mode: SerializationMode = SerializationMode.FOR_BROADCAST) : JsonAdapter<UpdateRecipe>() {
    override fun fromJson(p0: JsonReader): UpdateRecipe? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @ToJson
    override fun toJson(p0: JsonWriter, p1: UpdateRecipe?) {
        JsonModelSerializer.serialize(mode, p0, p1)
    }

}